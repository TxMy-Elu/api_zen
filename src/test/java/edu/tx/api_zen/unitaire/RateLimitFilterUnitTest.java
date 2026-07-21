package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.config.RateLimitFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

/**
 * Tests unitaires du filtre de limitation de débit (rate limiting).
 *
 * Le filtre protège deux points d'entrée sensibles contre le brute-force :
 *   - POST /api/auth/login           : 5 requêtes / minute / IP
 *   - POST /api/auth/forgot-password : 3 requêtes / 10 minutes / IP
 * Au-delà, il répond 429 (Too Many Requests) sans transmettre la requête.
 */
class RateLimitFilterUnitTest {

    private static final String LOGIN = "/api/auth/login";
    private static final String FORGOT = "/api/auth/forgot-password";

    private RateLimitFilter filter;

    /** Chaîne de filtres factice : compte les passages au lieu d'appeler un servlet. */
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
        // une instance neuve par test : les compteurs repartent de zéro
        filter = new RateLimitFilter();
    }

    private MockHttpServletRequest request(String method, String uri, String ip) {
        MockHttpServletRequest req = new MockHttpServletRequest(method, uri);
        req.setRequestURI(uri);
        req.setRemoteAddr(ip);
        return req;
    }

    private MockHttpServletResponse send(MockHttpServletRequest req, FilterChain chain)
            throws ServletException, IOException {
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilter(req, res, chain);
        return res;
    }

    // ── Cas passants ─────────────────────────────────────────────────────────

    @Test
    void getRequestOnLogin_isNeverRateLimited() throws Exception {
        CountingChain chain = new CountingChain();
        for (int i = 0; i < 20; i++) {
            MockHttpServletResponse res = send(request("GET", LOGIN, "203.0.113.1"), chain);
            Assertions.assertEquals(200, res.getStatus());
        }
        Assertions.assertEquals(20, chain.calls(), "une requête GET ne doit jamais être limitée");
    }

    @Test
    void postOnAnotherUri_isNotRateLimited() throws Exception {
        CountingChain chain = new CountingChain();
        for (int i = 0; i < 20; i++) {
            send(request("POST", "/api/articles", "203.0.113.2"), chain);
        }
        Assertions.assertEquals(20, chain.calls(), "seuls login et forgot-password sont limités");
    }

    // ── Limitation sur /api/auth/login ───────────────────────────────────────

    @Test
    void login_allowsFiveAttemptsThenBlocksTheSixth() throws Exception {
        CountingChain chain = new CountingChain();
        String ip = "203.0.113.10";

        for (int i = 1; i <= 5; i++) {
            MockHttpServletResponse res = send(request("POST", LOGIN, ip), chain);
            Assertions.assertEquals(200, res.getStatus(), "tentative " + i + " doit passer");
        }
        Assertions.assertEquals(5, chain.calls());

        MockHttpServletResponse blocked = send(request("POST", LOGIN, ip), chain);
        Assertions.assertEquals(429, blocked.getStatus(), "la 6e tentative doit être rejetée");
        Assertions.assertEquals(5, chain.calls(), "la requête rejetée ne doit pas atteindre la suite");
        Assertions.assertTrue(blocked.getContentAsString().contains("Trop de tentatives"));
    }

    @Test
    void login_isCaseInsensitiveOnHttpMethod() throws Exception {
        CountingChain chain = new CountingChain();
        String ip = "203.0.113.11";
        for (int i = 0; i < 5; i++) {
            send(request("post", LOGIN, ip), chain);
        }
        MockHttpServletResponse blocked = send(request("post", LOGIN, ip), chain);
        Assertions.assertEquals(429, blocked.getStatus(), "\"post\" en minuscules doit aussi être limité");
    }

    // ── Limitation sur /api/auth/forgot-password ─────────────────────────────

    @Test
    void forgotPassword_allowsThreeRequestsThenBlocksTheFourth() throws Exception {
        CountingChain chain = new CountingChain();
        String ip = "203.0.113.20";

        for (int i = 1; i <= 3; i++) {
            MockHttpServletResponse res = send(request("POST", FORGOT, ip), chain);
            Assertions.assertEquals(200, res.getStatus(), "demande " + i + " doit passer");
        }

        MockHttpServletResponse blocked = send(request("POST", FORGOT, ip), chain);
        Assertions.assertEquals(429, blocked.getStatus(), "la 4e demande doit être rejetée");
        Assertions.assertEquals(3, chain.calls());
        Assertions.assertTrue(blocked.getContentAsString().contains("Trop de demandes"));
    }

    @Test
    void loginAndForgotPassword_haveIndependentQuotas() throws Exception {
        CountingChain chain = new CountingChain();
        String ip = "203.0.113.21";

        for (int i = 0; i < 5; i++) {
            send(request("POST", LOGIN, ip), chain);
        }
        Assertions.assertEquals(429, send(request("POST", LOGIN, ip), chain).getStatus());

        // le quota de forgot-password ne doit pas avoir été entamé
        Assertions.assertEquals(200, send(request("POST", FORGOT, ip), chain).getStatus());
    }

    // ── Identification du client ─────────────────────────────────────────────

    @Test
    void quotasAreTrackedPerIpAddress() throws Exception {
        CountingChain chain = new CountingChain();
        for (int i = 0; i < 5; i++) {
            send(request("POST", LOGIN, "203.0.113.30"), chain);
        }
        Assertions.assertEquals(429, send(request("POST", LOGIN, "203.0.113.30"), chain).getStatus());

        MockHttpServletResponse other = send(request("POST", LOGIN, "203.0.113.31"), chain);
        Assertions.assertEquals(200, other.getStatus(), "une autre IP garde son propre quota");
    }

    @Test
    void xForwardedFor_identifiesTheClientBehindTheProxy() throws Exception {
        CountingChain chain = new CountingChain();

        // même IP de proxy (remoteAddr), mais deux clients distincts derrière
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest req = request("POST", LOGIN, "10.0.0.1");
            req.addHeader("X-Forwarded-For", "203.0.113.40, 10.0.0.1");
            send(req, chain);
        }
        MockHttpServletRequest sameClient = request("POST", LOGIN, "10.0.0.1");
        sameClient.addHeader("X-Forwarded-For", "203.0.113.40, 10.0.0.1");
        Assertions.assertEquals(429, send(sameClient, chain).getStatus(),
                "le premier maillon de X-Forwarded-For identifie le client");

        MockHttpServletRequest otherClient = request("POST", LOGIN, "10.0.0.1");
        otherClient.addHeader("X-Forwarded-For", "203.0.113.41");
        Assertions.assertEquals(200, send(otherClient, chain).getStatus(),
                "un autre client derrière le même proxy garde son quota");
    }

    @Test
    void emptyXForwardedFor_fallsBackToRemoteAddress() throws Exception {
        CountingChain chain = new CountingChain();
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest req = request("POST", LOGIN, "203.0.113.50");
            req.addHeader("X-Forwarded-For", "");
            send(req, chain);
        }
        MockHttpServletRequest req = request("POST", LOGIN, "203.0.113.50");
        req.addHeader("X-Forwarded-For", "");
        Assertions.assertEquals(429, send(req, chain).getStatus(),
                "un en-tête vide doit faire retomber sur remoteAddr");
    }

    // ── Forme de la réponse de rejet ─────────────────────────────────────────

    @Test
    void rejection_returnsJsonErrorPayload() throws Exception {
        CountingChain chain = new CountingChain();
        String ip = "203.0.113.60";
        for (int i = 0; i < 5; i++) {
            send(request("POST", LOGIN, ip), chain);
        }

        MockHttpServletResponse res = send(request("POST", LOGIN, ip), chain);

        Assertions.assertEquals(429, res.getStatus());
        Assertions.assertTrue(res.getContentType().startsWith("application/json"),
                "content-type inattendu : " + res.getContentType());
        Assertions.assertEquals("UTF-8", res.getCharacterEncoding());
        Assertions.assertTrue(res.getContentAsString().startsWith("{\"error\":\""));
        Assertions.assertTrue(res.getContentAsString().endsWith("\"}"));
    }
}
