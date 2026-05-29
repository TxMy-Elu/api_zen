package edu.tx.api_zen.controller;

import edu.tx.api_zen.dto.*;
import edu.tx.api_zen.service.AuthService;
import edu.tx.api_zen.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoints de connexion, inscription et reinitialisation de mot de passe")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIp(httpRequest);
            AuthResponse response = authService.login(request, ipAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou mot de passe incorrect"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription utilisateur")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIp(httpRequest);
            AuthResponse response = authService.register(request, ipAddress);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Demande de lien de reinitialisation")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Si l'email existe, un lien de réinitialisation a été envoyé"));
    }

    @GetMapping("/reset-password/validate")
    @Operation(summary = "Validation d'un token de reinitialisation")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        if (valid) {
            return ResponseEntity.ok(Map.of("valid", true));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("valid", false, "error", "Token invalide ou expiré"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reinitialisation du mot de passe")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        boolean success = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Token invalide ou expiré"));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

