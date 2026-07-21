package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.security.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Tests unitaires de l'émission et de la vérification des jetons JWT.
 *
 * C'est le code qui décide qui entre dans l'application : un jeton accepté à tort
 * vaut une authentification contournée. Les cas de rejet sont donc au moins aussi
 * importants que le cas passant.
 */
class JwtUtilsUnitTest {

    /** Secret de test — 256 bits minimum, imposé par HS256. */
    private static final String SECRET = "secret-de-test-suffisamment-long-pour-hs256-256-bits";
    private static final String AUTRE_SECRET = "un-tout-autre-secret-egalement-long-pour-hs256-256b";
    private static final long UNE_HEURE = 3_600_000L;

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        setSecret(jwtUtils, SECRET);
        setExpiration(jwtUtils, UNE_HEURE);
    }

    private static void setSecret(JwtUtils utils, String secret) {
        ReflectionTestUtils.setField(utils, "jwtSecret", secret);
    }

    private static void setExpiration(JwtUtils utils, long ms) {
        ReflectionTestUtils.setField(utils, "jwtExpirationMs", ms);
    }

    private static SecretKey key(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ── Émission et relecture ────────────────────────────────────────────────

    @Test
    void generatedToken_carriesEmailUserIdAndRole() {
        String token = jwtUtils.generateToken("tom@cesizen.fr", 42, "ROLE_ADMIN");

        Assertions.assertEquals("tom@cesizen.fr", jwtUtils.getEmailFromToken(token));
        Assertions.assertEquals(42, jwtUtils.getUserIdFromToken(token));
        Assertions.assertEquals("ROLE_ADMIN", jwtUtils.getRoleFromToken(token));
    }

    @Test
    void generatedToken_hasThreeBase64Parts() {
        String token = jwtUtils.generateToken("tom@cesizen.fr", 1, "ROLE_USER");

        Assertions.assertEquals(3, token.split("\\.").length,
                "un JWT compact est composé de header.payload.signature");
    }

    @Test
    void generatedToken_isAcceptedByValidateToken() {
        String token = jwtUtils.generateToken("tom@cesizen.fr", 1, "ROLE_USER");

        Assertions.assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void twoTokensForDifferentUsers_areDistinct() {
        String premier = jwtUtils.generateToken("a@cesizen.fr", 1, "ROLE_USER");
        String second = jwtUtils.generateToken("b@cesizen.fr", 2, "ROLE_USER");

        Assertions.assertNotEquals(premier, second);
    }

    // ── Rejets : c'est ici que se joue la sécurité ───────────────────────────

    @Test
    void expiredToken_isRejected() {
        // jeton déjà expiré au moment où il est émis
        setExpiration(jwtUtils, -1_000L);
        String expire = jwtUtils.generateToken("tom@cesizen.fr", 1, "ROLE_USER");

        setExpiration(jwtUtils, UNE_HEURE);
        Assertions.assertFalse(jwtUtils.validateToken(expire),
                "un jeton dont la date d'expiration est passée doit être refusé");
    }

    @Test
    void tokenSignedWithAnotherSecret_isRejected() {
        JwtUtils attaquant = new JwtUtils();
        setSecret(attaquant, AUTRE_SECRET);
        setExpiration(attaquant, UNE_HEURE);

        String forge = attaquant.generateToken("admin@cesizen.fr", 1, "ROLE_ADMIN");

        Assertions.assertFalse(jwtUtils.validateToken(forge),
                "un jeton signé avec une autre clé ne doit pas être accepté");
    }

    @Test
    void tokenWithTamperedPayload_isRejected() {
        String token = jwtUtils.generateToken("user@cesizen.fr", 1, "ROLE_USER");
        String[] parts = token.split("\\.");

        // on remplace le payload par un autre, la signature ne correspond plus
        String autrePayload = Jwts.builder()
                .subject("user@cesizen.fr")
                .claim("userId", 1)
                .claim("role", "ROLE_ADMIN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + UNE_HEURE))
                .signWith(key(AUTRE_SECRET))
                .compact()
                .split("\\.")[1];

        String falsifie = parts[0] + "." + autrePayload + "." + parts[2];

        Assertions.assertFalse(jwtUtils.validateToken(falsifie),
                "modifier le rôle dans le payload doit invalider la signature");
    }

    @Test
    void unsignedToken_isRejected() {
        String sansSignature = Jwts.builder()
                .subject("tom@cesizen.fr")
                .claim("role", "ROLE_ADMIN")
                .compact();

        Assertions.assertFalse(jwtUtils.validateToken(sansSignature),
                "un jeton non signé (alg=none) ne doit jamais être accepté");
    }

    @Test
    void malformedToken_isRejectedWithoutThrowing() {
        Assertions.assertFalse(jwtUtils.validateToken("ceci-nest-pas-un-jwt"));
        Assertions.assertFalse(jwtUtils.validateToken("a.b.c"));
        Assertions.assertFalse(jwtUtils.validateToken(""));
    }

    @Test
    void nullToken_isRejectedWithoutThrowing() {
        Assertions.assertFalse(jwtUtils.validateToken(null),
                "validateToken doit absorber l'entrée nulle, pas propager l'exception");
    }

    // ── Cohérence entre validation et lecture des claims ─────────────────────

    @Test
    void readingClaimsOfATokenSignedElsewhere_throws() {
        JwtUtils autre = new JwtUtils();
        setSecret(autre, AUTRE_SECRET);
        setExpiration(autre, UNE_HEURE);
        String forge = autre.generateToken("admin@cesizen.fr", 1, "ROLE_ADMIN");

        Assertions.assertThrows(io.jsonwebtoken.JwtException.class,
                () -> jwtUtils.getEmailFromToken(forge),
                "la lecture des claims ne doit pas être silencieuse sur un jeton invalide");
    }

    @Test
    void userIdIsReadBackAsInteger() {
        String token = jwtUtils.generateToken("tom@cesizen.fr", 12345, "ROLE_USER");

        Integer userId = jwtUtils.getUserIdFromToken(token);

        Assertions.assertNotNull(userId);
        Assertions.assertEquals(12345, userId.intValue());
    }
}
