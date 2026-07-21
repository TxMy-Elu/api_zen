package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.ForgotPasswordRequest;
import edu.tx.api_zen.dto.LoginRequest;
import edu.tx.api_zen.dto.ResetPasswordRequest;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Tests unitaires des objets de requête du parcours d'authentification :
 * connexion, demande de réinitialisation, réinitialisation effective.
 */
class AuthRequestDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private static <T> boolean violation(Set<ConstraintViolation<T>> v, String champ, Class<?> contrainte) {
        return v.stream().anyMatch(c -> c.getPropertyPath().toString().equals(champ)
                && contrainte.isInstance(c.getConstraintDescriptor().getAnnotation()));
    }

    // ── Connexion ────────────────────────────────────────────────────────────

    @Nested
    class Connexion {

        private LoginRequest valide() {
            LoginRequest r = new LoginRequest();
            r.setEmail("camille.martin@example.com");
            r.setPassword("MotDePasse123!");
            return r;
        }

        @Test
        void completeRequest_isValid() {
            Assertions.assertTrue(validator.validate(valide()).isEmpty());
        }

        @Test
        void accessorsRoundTripTheValues() {
            LoginRequest r = valide();

            Assertions.assertEquals("camille.martin@example.com", r.getEmail());
            Assertions.assertEquals("MotDePasse123!", r.getPassword());
        }

        @Test
        void missingEmail_isRejected() {
            LoginRequest r = valide();
            r.setEmail(null);

            Assertions.assertTrue(violation(validator.validate(r), "email", NotBlank.class));
        }

        @Test
        void malformedEmail_isRejected() {
            LoginRequest r = valide();
            r.setEmail("pas-un-email");

            Assertions.assertTrue(violation(validator.validate(r), "email", Email.class));
        }

        @Test
        void missingPassword_isRejected() {
            LoginRequest r = valide();
            r.setPassword("");

            Assertions.assertTrue(violation(validator.validate(r), "password", NotBlank.class));
        }

        @Test
        void weakPasswordIsAcceptedAtLogin() {
            // La politique de mot de passe s'applique à l'inscription, pas à la
            // connexion : refuser ici empêcherait un ancien compte de se connecter
            // pour aller changer son mot de passe.
            LoginRequest r = valide();
            r.setPassword("abc");

            Assertions.assertTrue(validator.validate(r).isEmpty());
        }
    }

    // ── Demande de réinitialisation ──────────────────────────────────────────

    @Nested
    class DemandeDeReinitialisation {

        @Test
        void validEmail_isAccepted() {
            Assertions.assertTrue(
                    validator.validate(new ForgotPasswordRequest("camille.martin@example.com")).isEmpty());
        }

        @Test
        void blankEmail_isRejected() {
            Assertions.assertTrue(
                    violation(validator.validate(new ForgotPasswordRequest("  ")), "email", NotBlank.class));
        }

        @Test
        void malformedEmail_isRejected() {
            Assertions.assertTrue(
                    violation(validator.validate(new ForgotPasswordRequest("camille@")), "email", Email.class));
        }

        @Test
        void noArgsConstructorAndAccessors() {
            ForgotPasswordRequest r = new ForgotPasswordRequest();
            r.setEmail("camille.martin@example.com");

            Assertions.assertEquals("camille.martin@example.com", r.getEmail());
            Assertions.assertEquals(new ForgotPasswordRequest("camille.martin@example.com"), r);
        }
    }

    // ── Réinitialisation effective ───────────────────────────────────────────

    @Nested
    class Reinitialisation {

        private ResetPasswordRequest valide() {
            return new ResetPasswordRequest("123e4567-e89b-12d3-a456-426614174000", "NouveauMotDePasse123!");
        }

        @Test
        void completeRequest_isValid() {
            Assertions.assertTrue(validator.validate(valide()).isEmpty());
        }

        @Test
        void missingToken_isRejected() {
            ResetPasswordRequest r = valide();
            r.setToken(null);

            Assertions.assertTrue(violation(validator.validate(r), "token", NotBlank.class));
        }

        @Test
        void passwordTooShort_isRejected() {
            ResetPasswordRequest r = valide();
            r.setNewPassword("Ab1!c");

            Assertions.assertTrue(violation(validator.validate(r), "newPassword", Size.class));
        }

        @Test
        void passwordWithoutDigit_isRejected() {
            ResetPasswordRequest r = valide();
            r.setNewPassword("NouveauMotDePasse!");

            Assertions.assertTrue(violation(validator.validate(r), "newPassword", Pattern.class));
        }

        @Test
        void passwordWithoutSpecialCharacter_isRejected() {
            ResetPasswordRequest r = valide();
            r.setNewPassword("NouveauMotDePasse123");

            Assertions.assertTrue(violation(validator.validate(r), "newPassword", Pattern.class));
        }

        @Test
        void policyMatchesTheOneAppliedAtRegistration() {
            // Les deux points d'entrée doivent imposer exactement la même règle :
            // sinon la réinitialisation devient un contournement de la politique.
            ResetPasswordRequest r = valide();
            r.setNewPassword("MotDePasse");

            long patterns = validator.validate(r).stream()
                    .filter(c -> c.getPropertyPath().toString().equals("newPassword"))
                    .filter(c -> c.getConstraintDescriptor().getAnnotation() instanceof Pattern)
                    .count();

            Assertions.assertEquals(2, patterns);
        }

        @Test
        void accessorsRoundTripTheValues() {
            ResetPasswordRequest r = valide();

            Assertions.assertEquals("123e4567-e89b-12d3-a456-426614174000", r.getToken());
            Assertions.assertEquals("NouveauMotDePasse123!", r.getNewPassword());
        }
    }
}
