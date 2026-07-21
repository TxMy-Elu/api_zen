package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.ArticleDto;
import edu.tx.api_zen.dto.AuthResponse;
import edu.tx.api_zen.dto.CategorieDto;
import edu.tx.api_zen.dto.ConsulterDto;
import edu.tx.api_zen.dto.ExercerDto;
import edu.tx.api_zen.dto.ExerciceDto;
import edu.tx.api_zen.dto.LogActiviteDto;
import edu.tx.api_zen.dto.LogConnexionDto;
import edu.tx.api_zen.dto.ParticipationRequest;
import edu.tx.api_zen.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

/**
 * Tests unitaires des objets de transfert renvoyés par l'API.
 *
 * Ces classes ne portent pas de logique : ce qui est vérifié ici, c'est qu'elles
 * transportent fidèlement leurs valeurs, et surtout qu'elles n'exposent que ce
 * qu'elles doivent — le point sensible étant l'absence de mot de passe dans
 * UserDto et le type « Bearer » imposé dans AuthResponse.
 */
class ReponseDtoUnitTest {

    private static final Instant T = Instant.parse("2026-03-30T10:15:30Z");

    // ── Réponse d'authentification ───────────────────────────────────────────

    @Test
    void authResponse_carriesTheIdentityAndDefaultsToBearer() {
        AuthResponse r = new AuthResponse("jeton.jwt.signature", 12,
                "camille.martin@example.com", "Martin", "Camille", "ROLE_USER");

        Assertions.assertEquals("jeton.jwt.signature", r.getToken());
        Assertions.assertEquals(12, r.getUserId());
        Assertions.assertEquals("camille.martin@example.com", r.getEmail());
        Assertions.assertEquals("Martin", r.getNom());
        Assertions.assertEquals("Camille", r.getPrenom());
        Assertions.assertEquals("ROLE_USER", r.getRole());
        Assertions.assertEquals("Bearer", r.getType(),
                "le type doit valoir Bearer sans avoir à être renseigné");
    }

    @Test
    void authResponse_accessorsAreWritable() {
        AuthResponse r = new AuthResponse(null, null, null, null, null, null);
        r.setToken("autre.jeton");
        r.setType("Bearer");
        r.setUserId(7);
        r.setEmail("admin@cesizen.fr");
        r.setNom("Doguet");
        r.setPrenom("Tom");
        r.setRole("ROLE_ADMIN");

        Assertions.assertEquals("autre.jeton", r.getToken());
        Assertions.assertEquals(7, r.getUserId());
        Assertions.assertEquals("ROLE_ADMIN", r.getRole());
    }

    // ── Utilisateur ──────────────────────────────────────────────────────────

    @Test
    void userDto_roundTripsItsFields() {
        UserDto u = new UserDto();
        u.setId(12);
        u.setNom("Martin");
        u.setPrenom("Camille");
        u.setEmail("camille.martin@example.com");
        u.setActive(true);
        u.setCreationDate(T);
        u.setRoleId(2);
        u.setRoleLibelle("ROLE_USER");

        Assertions.assertEquals(12, u.getId());
        Assertions.assertEquals("Martin", u.getNom());
        Assertions.assertEquals("Camille", u.getPrenom());
        Assertions.assertEquals("camille.martin@example.com", u.getEmail());
        Assertions.assertTrue(u.getActive());
        Assertions.assertEquals(T, u.getCreationDate());
        Assertions.assertEquals(2, u.getRoleId());
        Assertions.assertEquals("ROLE_USER", u.getRoleLibelle());
    }

    @Test
    void userDto_exposesNoPasswordAccessor() {
        boolean fuite = java.util.Arrays.stream(UserDto.class.getMethods())
                .map(java.lang.reflect.Method::getName)
                .anyMatch(n -> n.toLowerCase().contains("password"));

        Assertions.assertFalse(fuite,
                "le hachage du mot de passe ne doit jamais pouvoir sortir par UserDto");
    }

    // ── Contenus ─────────────────────────────────────────────────────────────

    @Test
    void articleDto_roundTripsItsFields() {
        ArticleDto a = new ArticleDto();
        a.setIdArticle(1);
        a.setTitre("Introduction à la respiration carrée");
        a.setContenu("Découvrez les bases…");
        a.setTypeMedia("video");
        a.setMediaUrl("/uploads/tuto.mp4");
        a.setDatePublication(T);
        a.setDateModification(null);
        a.setEstPublie(true);
        a.setIdCategorie(1);
        a.setCategorieLibelle("Tuto Vidéo");

        Assertions.assertEquals(1, a.getIdArticle());
        Assertions.assertEquals("Introduction à la respiration carrée", a.getTitre());
        Assertions.assertEquals("Découvrez les bases…", a.getContenu());
        Assertions.assertEquals("video", a.getTypeMedia());
        Assertions.assertEquals("/uploads/tuto.mp4", a.getMediaUrl());
        Assertions.assertEquals(T, a.getDatePublication());
        Assertions.assertNull(a.getDateModification());
        Assertions.assertTrue(a.getEstPublie());
        Assertions.assertEquals(1, a.getIdCategorie());
        Assertions.assertEquals("Tuto Vidéo", a.getCategorieLibelle());
    }

    @Test
    void categorieDto_roundTripsItsFields() {
        CategorieDto c = new CategorieDto();
        c.setIdCategorie(3);
        c.setLibelle("Respiration");
        c.setDescription("Exercices guidés");

        Assertions.assertEquals(3, c.getIdCategorie());
        Assertions.assertEquals("Respiration", c.getLibelle());
        Assertions.assertEquals("Exercices guidés", c.getDescription());
    }

    @Test
    void exerciceDto_roundTripsItsDurations() {
        ExerciceDto e = new ExerciceDto();
        e.setIdExercice(4);
        e.setNom("Cohérence cardiaque");
        e.setDureeInspiration(5);
        e.setDureeApnee(0);
        e.setDureeExpiration(5);
        e.setDureeSession(300);
        e.setDescription("5-0-5 pendant 5 minutes");

        Assertions.assertEquals(4, e.getIdExercice());
        Assertions.assertEquals("Cohérence cardiaque", e.getNom());
        Assertions.assertEquals(5, e.getDureeInspiration());
        Assertions.assertEquals(0, e.getDureeApnee());
        Assertions.assertEquals(5, e.getDureeExpiration());
        Assertions.assertEquals(300, e.getDureeSession());
        Assertions.assertEquals("5-0-5 pendant 5 minutes", e.getDescription());
    }

    // ── Suivi d'activité ─────────────────────────────────────────────────────

    @Test
    void consulterDto_roundTripsItsFields() {
        ConsulterDto c = new ConsulterDto();
        c.setIdConsulter(9);
        c.setIdUtilisateur(12);
        c.setNomUtilisateur("Martin");
        c.setIdArticle(1);
        c.setTitreArticle("Introduction à la respiration carrée");
        c.setViewedAt(T);

        Assertions.assertEquals(9, c.getIdConsulter());
        Assertions.assertEquals(12, c.getIdUtilisateur());
        Assertions.assertEquals("Martin", c.getNomUtilisateur());
        Assertions.assertEquals(1, c.getIdArticle());
        Assertions.assertEquals("Introduction à la respiration carrée", c.getTitreArticle());
        Assertions.assertEquals(T, c.getViewedAt());
    }

    @Test
    void exercerDto_nestsUserAndExercice() {
        UserDto u = new UserDto();
        u.setId(12);
        ExerciceDto e = new ExerciceDto();
        e.setIdExercice(4);

        ExercerDto x = new ExercerDto();
        x.setIdExercer(21);
        x.setUser(u);
        x.setExercice(e);
        x.setCompletedAt(T);

        Assertions.assertEquals(21, x.getIdExercer());
        Assertions.assertEquals(12, x.getUser().getId());
        Assertions.assertEquals(4, x.getExercice().getIdExercice());
        Assertions.assertEquals(T, x.getCompletedAt());
    }

    @Test
    void participationRequest_roundTripsItsFields() {
        ParticipationRequest p = new ParticipationRequest();
        p.setUserId(12);
        p.setCompletedAt(T);

        Assertions.assertEquals(12, p.getUserId());
        Assertions.assertEquals(T, p.getCompletedAt());
    }

    // ── Journaux d'audit ─────────────────────────────────────────────────────

    @Test
    void logActiviteDto_roundTripsItsFields() {
        LogActiviteDto l = new LogActiviteDto();
        l.setIdLogActivite(100);
        l.setDateAction(T);
        l.setTypeAction("DELETE");
        l.setTableConcernee("article");
        l.setIdEnregistrement(1);
        l.setDetails("Suppression de l'article 1");
        l.setAdresseIp("203.0.113.10");
        l.setIdUtilisateur(12);
        l.setNomUtilisateur("Martin");

        Assertions.assertEquals(100, l.getIdLogActivite());
        Assertions.assertEquals(T, l.getDateAction());
        Assertions.assertEquals("DELETE", l.getTypeAction());
        Assertions.assertEquals("article", l.getTableConcernee());
        Assertions.assertEquals(1, l.getIdEnregistrement());
        Assertions.assertEquals("Suppression de l'article 1", l.getDetails());
        Assertions.assertEquals("203.0.113.10", l.getAdresseIp());
        Assertions.assertEquals(12, l.getIdUtilisateur());
        Assertions.assertEquals("Martin", l.getNomUtilisateur());
    }

    @Test
    void logConnexionDto_distinguishesSuccessFromFailure() {
        LogConnexionDto succes = new LogConnexionDto();
        succes.setIdLogConnexion(1);
        succes.setDateConnexion(T);
        succes.setAdresseIp("203.0.113.10");
        succes.setReussite(true);
        succes.setEmailTente("camille.martin@example.com");

        Assertions.assertTrue(succes.getReussite());
        Assertions.assertNull(succes.getMotifEchec(), "un succès n'a pas de motif d'échec");

        LogConnexionDto echec = new LogConnexionDto();
        echec.setReussite(false);
        echec.setMotifEchec("Mot de passe incorrect");
        echec.setEmailTente("inconnu@example.com");

        Assertions.assertFalse(echec.getReussite());
        Assertions.assertEquals("Mot de passe incorrect", echec.getMotifEchec());
        Assertions.assertEquals("inconnu@example.com", echec.getEmailTente());
    }

    @Test
    void logConnexionDto_exposesNoPasswordAccessor() {
        boolean fuite = java.util.Arrays.stream(LogConnexionDto.class.getMethods())
                .map(java.lang.reflect.Method::getName)
                .anyMatch(n -> n.toLowerCase().contains("password") || n.toLowerCase().contains("motdepasse"));

        Assertions.assertFalse(fuite,
                "le journal de connexion ne doit jamais conserver le mot de passe tenté");
    }
}
