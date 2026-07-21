package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.model.Role;
import edu.tx.api_zen.model.User;
import edu.tx.api_zen.security.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Tests unitaires du chargement d'un utilisateur pour Spring Security.
 *
 * Cette classe fait le pont entre la table `user` et le modèle de sécurité :
 * une erreur ici se traduit soit par un accès refusé à tort, soit — plus grave —
 * par un rôle mal attribué.
 */
class CustomUserDetailsServiceUnitTest {

    private UserDao userDao;
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        userDao = Mockito.mock(UserDao.class);
        service = new CustomUserDetailsService(userDao);
    }

    private static User utilisateur(String email, String role, boolean actif) {
        Role r = new Role();
        r.setLibelle(role);

        User u = new User();
        u.setEmail(email);
        u.setPassword("$2a$10$hachage-bcrypt-factice");
        u.setActive(actif);
        u.setRole(r);
        return u;
    }

    // ── Cas nominal ──────────────────────────────────────────────────────────

    @Test
    void knownUser_isMappedWithEmailPasswordAndAuthority() {
        Mockito.when(userDao.findByEmail("tom@cesizen.fr"))
                .thenReturn(Optional.of(utilisateur("tom@cesizen.fr", "ROLE_USER", true)));

        UserDetails details = service.loadUserByUsername("tom@cesizen.fr");

        Assertions.assertEquals("tom@cesizen.fr", details.getUsername());
        Assertions.assertEquals("$2a$10$hachage-bcrypt-factice", details.getPassword());
        Assertions.assertEquals(1, details.getAuthorities().size());
        Assertions.assertEquals("ROLE_USER",
                details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void adminRole_isPropagatedAsAuthority() {
        Mockito.when(userDao.findByEmail("admin@cesizen.fr"))
                .thenReturn(Optional.of(utilisateur("admin@cesizen.fr", "ROLE_ADMIN", true)));

        UserDetails details = service.loadUserByUsername("admin@cesizen.fr");

        Assertions.assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())));
    }

    @Test
    void activeUser_isEnabled() {
        Mockito.when(userDao.findByEmail("actif@cesizen.fr"))
                .thenReturn(Optional.of(utilisateur("actif@cesizen.fr", "ROLE_USER", true)));

        Assertions.assertTrue(service.loadUserByUsername("actif@cesizen.fr").isEnabled());
    }

    // ── Cas de refus ─────────────────────────────────────────────────────────

    @Test
    void deactivatedUser_isDisabled() {
        Mockito.when(userDao.findByEmail("desactive@cesizen.fr"))
                .thenReturn(Optional.of(utilisateur("desactive@cesizen.fr", "ROLE_USER", false)));

        UserDetails details = service.loadUserByUsername("desactive@cesizen.fr");

        Assertions.assertFalse(details.isEnabled(),
                "un compte désactivé ne doit pas pouvoir s'authentifier");
    }

    @Test
    void unknownEmail_throwsUsernameNotFound() {
        Mockito.when(userDao.findByEmail("inconnu@cesizen.fr")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("inconnu@cesizen.fr"));

        Assertions.assertTrue(ex.getMessage().contains("inconnu@cesizen.fr"));
    }

    @Test
    void lookupIsDelegatedToTheDaoExactlyOnce() {
        Mockito.when(userDao.findByEmail("tom@cesizen.fr"))
                .thenReturn(Optional.of(utilisateur("tom@cesizen.fr", "ROLE_USER", true)));

        service.loadUserByUsername("tom@cesizen.fr");

        Mockito.verify(userDao, Mockito.times(1)).findByEmail("tom@cesizen.fr");
        Mockito.verifyNoMoreInteractions(userDao);
    }
}
