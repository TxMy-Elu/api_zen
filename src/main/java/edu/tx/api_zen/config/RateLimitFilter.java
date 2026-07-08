package edu.tx.api_zen.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> forgotBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method)) {
            if ("/api/auth/login".equals(uri)) {
                Bucket bucket = loginBuckets.computeIfAbsent(getIp(request), ip -> buildBucket(5, Duration.ofMinutes(1)));
                if (!bucket.tryConsume(1)) {
                    reject(response, "Trop de tentatives de connexion. Réessayez dans 1 minute.");
                    return;
                }
            } else if ("/api/auth/forgot-password".equals(uri)) {
                Bucket bucket = forgotBuckets.computeIfAbsent(getIp(request), ip -> buildBucket(3, Duration.ofMinutes(10)));
                if (!bucket.tryConsume(1)) {
                    reject(response, "Trop de demandes de réinitialisation. Réessayez dans 10 minutes.");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private Bucket buildBucket(int capacity, Duration period) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(capacity, period)
                        .build())
                .build();
    }

    private String getIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
