package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.security.CustomUserDetailsService;
import edu.tx.api_zen.security.JwtAuthenticationFilter;
import edu.tx.api_zen.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

/**
 * Tests unitaires du filtre qui installe l'authentification à partir du jeton.
 *
 * Deux invariants sont vérifiés systématiquement :
 *   - la chaîne de filtres est toujours poursuivie, même en cas de rejet
 *     (c'est Spring Security, plus loin, qui décidera du 401 ou du 403) ;
 *   - le contexte de sécurité n'est renseigné que sur un jeton valide
 *     correspondant à un utilisateur existant.
 */
class JwtAuthenticationFilterUnitTest {

    private static final String EMAIL = "tom@cesizen.fr";
    private static final String JETON = "un.jeton.quelconque";

    private JwtUtils jwtUtils;
    private CustomUserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;

    /** Chaîne de filtres factice : compte les passages. */
    private static class CountingChain implements FilterChain {
        private int calls;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            calls++;
        }

        int calls() {
            return calls;
        }
    }

    @BeforeEach
    void setUp() {
        jwtUtils = Mockito.mock(JwtUtils.class);
        userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        filter = new JwtAuthenticationFilter(jwtUtils, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // le contexte est porté par un ThreadLocal : sans nettoyage il fuit d'un test à l'autre
        SecurityContextHolder.clearContext();
    }

    private static UserDetails details() {
        return new User(EMAIL, "hachage", true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private CountingChain run(String authHeader) throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/user/me");
        if (authHeader != null) {
            req.addHeader("Authorization", authHeader);
        }
        CountingChain chain = new CountingChain();
        filter.doFilter(req, new MockHttpServletResponse(), chain);
        return chain;
    }

    private static boolean estAuthentifie() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    // ── Requêtes qui ne portent pas de jeton ─────────────────────────────────

    @Test
    void withoutAuthorizationHeader_chainContinuesAndContextStaysEmpty() throws Exception {
        CountingChain chain = run(null);

        Assertions.assertEquals(1, chain.calls(), "la requête doit poursuivre son chemin");
        Assertions.assertFalse(estAuthentifie());
        Mockito.verifyNoInteractions(jwtUtils, userDetailsService);
    }

    @Test
    void nonBearerScheme_isIgnored() throws Exception {
        CountingChain chain = run("Basic dG9tOm1vdGRlcGFzc2U=");

        Assertions.assertEquals(1, chain.calls());
        Assertions.assertFalse(estAuthentifie());
        Mockito.verifyNoInteractions(jwtUtils, userDetailsService);
    }

    // ── Jeton présent mais refusé ────────────────────────────────────────────

    @Test
    void invalidToken_leavesContextEmptyButDoesNotBlock() throws Exception {
        Mockito.when(jwtUtils.validateToken(JETON)).thenReturn(false);

        CountingChain chain = run("Bearer " + JETON);

        Assertions.assertEquals(1, chain.calls(),
                "un jeton invalide ne doit pas interrompre la chaîne : le refus vient plus loin");
        Assertions.assertFalse(estAuthentifie());
        Mockito.verify(userDetailsService, Mockito.never()).loadUserByUsername(Mockito.anyString());
    }

    @Test
    void validTokenButDeletedUser_clearsContextAndContinues() throws Exception {
        Mockito.when(jwtUtils.validateToken(JETON)).thenReturn(true);
        Mockito.when(jwtUtils.getEmailFromToken(JETON)).thenReturn(EMAIL);
        Mockito.when(userDetailsService.loadUserByUsername(EMAIL))
                .thenThrow(new UsernameNotFoundException("Utilisateur non trouvé: " + EMAIL));

        CountingChain chain = run("Bearer " + JETON);

        Assertions.assertEquals(1, chain.calls());
        Assertions.assertFalse(estAuthentifie(),
                "un jeton encore valide dont le compte a été supprimé ne doit pas authentifier");
    }

    // ── Cas nominal ──────────────────────────────────────────────────────────

    @Test
    void validToken_populatesSecurityContext() throws Exception {
        Mockito.when(jwtUtils.validateToken(JETON)).thenReturn(true);
        Mockito.when(jwtUtils.getEmailFromToken(JETON)).thenReturn(EMAIL);
        Mockito.when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(details());

        CountingChain chain = run("Bearer " + JETON);

        Assertions.assertEquals(1, chain.calls());
        Assertions.assertTrue(estAuthentifie());
        Assertions.assertEquals(EMAIL,
                ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getUsername());
    }

    @Test
    void validToken_propagatesAuthorities() throws Exception {
        Mockito.when(jwtUtils.validateToken(JETON)).thenReturn(true);
        Mockito.when(jwtUtils.getEmailFromToken(JETON)).thenReturn(EMAIL);
        Mockito.when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(details());

        run("Bearer " + JETON);

        Assertions.assertTrue(SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> "ROLE_USER".equals(a.getAuthority())));
    }

    @Test
    void validToken_neverExposesCredentials() throws Exception {
        Mockito.when(jwtUtils.validateToken(JETON)).thenReturn(true);
        Mockito.when(jwtUtils.getEmailFromToken(JETON)).thenReturn(EMAIL);
        Mockito.when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(details());

        run("Bearer " + JETON);

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                "le mot de passe ne doit jamais être replacé dans le contexte");
    }

    @Test
    void bearerPrefixIsStrippedBeforeValidation() throws Exception {
        Mockito.when(jwtUtils.validateToken(Mockito.anyString())).thenReturn(false);

        run("Bearer " + JETON);

        Mockito.verify(jwtUtils).validateToken(JETON);
    }
}
