package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Tests unitaires des règles de validation à l'inscription.
 *
 * C'est le seul endroit où la politique de mot de passe est exprimée : longueur
 * minimale, au moins un chiffre, au moins un caractère spécial. Si une de ces
 * contraintes saute, rien d'autre ne la rattrape en aval.
 */
class RegisterRequestUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private static RegisterRequest valide() {
        RegisterRequest r = new RegisterRequest();
        r.setNom("Martin");
        r.setPrenom("Camille");
        r.setEmail("camille.martin@example.com");
        r.setPassword("MotDePasse123!");
        return r;
    }

    private static Set<ConstraintViolation<RegisterRequest>> valider(RegisterRequest r) {
        return validator.validate(r);
    }

    private static boolean violation(Set<ConstraintViolation<RegisterRequest>> v,
                                     String champ, Class<?> contrainte) {
        return v.stream().anyMatch(c -> c.getPropertyPath().toString().equals(champ)
                && contrainte.isInstance(c.getConstraintDescriptor().getAnnotation()));
    }

    // ── Cas nominal ──────────────────────────────────────────────────────────

    @Test
    void completeRequest_isValid() {
        Assertions.assertTrue(valider(valide()).isEmpty());
    }

    @Test
    void accessorsRoundTripTheValues() {
        RegisterRequest r = valide();

        Assertions.assertEquals("Martin", r.getNom());
        Assertions.assertEquals("Camille", r.getPrenom());
        Assertions.assertEquals("camille.martin@example.com", r.getEmail());
        Assertions.assertEquals("MotDePasse123!", r.getPassword());
    }

    // ── Champs obligatoires ──────────────────────────────────────────────────

    @Test
    void missingNom_isRejected() {
        RegisterRequest r = valide();
        r.setNom(null);

        Assertions.assertTrue(violation(valider(r), "nom", NotBlank.class));
    }

    @Test
    void blankPrenom_isRejected() {
        RegisterRequest r = valide();
        r.setPrenom("   ");

        Assertions.assertTrue(violation(valider(r), "prenom", NotBlank.class));
    }

    @Test
    void missingEmail_isRejected() {
        RegisterRequest r = valide();
        r.setEmail(null);

        Assertions.assertTrue(violation(valider(r), "email", NotBlank.class));
    }

    @Test
    void malformedEmail_isRejected() {
        RegisterRequest r = valide();
        r.setEmail("camille.martin.example.com");

        Assertions.assertTrue(violation(valider(r), "email", Email.class));
    }

    // ── Politique de mot de passe ────────────────────────────────────────────

    @Test
    void passwordShorterThanSixCharacters_isRejected() {
        RegisterRequest r = valide();
        r.setPassword("Ab1!c");

        Assertions.assertTrue(violation(valider(r), "password", Size.class));
    }

    @Test
    void passwordOfExactlySixCharacters_isAccepted() {
        RegisterRequest r = valide();
        r.setPassword("Abc1!d");

        Assertions.assertTrue(valider(r).isEmpty(), "six caractères est la borne acceptée");
    }

    @Test
    void passwordWithoutDigit_isRejected() {
        RegisterRequest r = valide();
        r.setPassword("MotDePasse!");

        Assertions.assertTrue(violation(valider(r), "password", Pattern.class));
    }

    @Test
    void passwordWithoutSpecialCharacter_isRejected() {
        RegisterRequest r = valide();
        r.setPassword("MotDePasse123");

        Assertions.assertTrue(violation(valider(r), "password", Pattern.class));
    }

    @Test
    void passwordWithNeitherDigitNorSpecial_raisesBothPatternViolations() {
        RegisterRequest r = valide();
        r.setPassword("MotDePasse");

        long patterns = valider(r).stream()
                .filter(c -> c.getPropertyPath().toString().equals("password"))
                .filter(c -> c.getConstraintDescriptor().getAnnotation() instanceof Pattern)
                .count();

        Assertions.assertEquals(2, patterns, "les deux règles doivent remonter séparément");
    }

    @Test
    void digitOrSpecialAtAnyPosition_isAccepted() {
        for (String motDePasse : new String[]{"1MotDePasse!", "MotDePasse1!", "!MotDePasse1", "Mot1DePasse!"}) {
            RegisterRequest r = valide();
            r.setPassword(motDePasse);

            Assertions.assertTrue(valider(r).isEmpty(),
                    "la position du chiffre ou du caractère spécial ne doit pas compter : " + motDePasse);
        }
    }

    @Test
    void longPasswordOfLettersOnly_isRejectedQuickly() {
        // Les expressions régulières sont écrites en forme linéaire ([^x]*[x].*)
        // précisément pour qu'une longue entrée non conforme ne déclenche pas de
        // retour sur trace exponentiel (ReDoS).
        RegisterRequest r = valide();
        r.setPassword("a".repeat(5_000));

        long debut = System.nanoTime();
        Set<ConstraintViolation<RegisterRequest>> violations = valider(r);
        long dureeMs = (System.nanoTime() - debut) / 1_000_000;

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertTrue(dureeMs < 1_000,
                "la validation doit rester linéaire, durée observée : " + dureeMs + " ms");
    }
}
