package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.RoleDao;
import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.dto.AuthResponse;
import edu.tx.api_zen.dto.LoginRequest;
import edu.tx.api_zen.dto.RegisterRequest;
import edu.tx.api_zen.model.Role;
import edu.tx.api_zen.model.User;
import edu.tx.api_zen.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final LogConnexionService logConnexionService;
    private final EmailService emailService;

    public AuthResponse login(LoginRequest request, String ipAddress) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userDao.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Utilisateur non trouvé"));

            if (!user.getActive()) {
                logConnexionService.logConnexion(request.getEmail(), ipAddress, false, "Compte désactivé");
                throw new BadCredentialsException("Compte désactivé");
            }

            String token = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole().getLibelle());

            // Log connexion réussie
            logConnexionService.logConnexion(request.getEmail(), ipAddress, true, null);

            try {
                emailService.sendLoginConnectionEmail(user.getEmail(), user.getPrenom(), ipAddress);
            } catch (RuntimeException e) {
                // Ne bloque pas la connexion si l'email de notification ne peut pas etre envoye.
                log.warn("Connexion reussie mais echec d'envoi de l'email de notification pour {}", user.getEmail(), e);
            }

            return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getRole().getLibelle()
            );
        } catch (BadCredentialsException e) {
            logConnexionService.logConnexion(request.getEmail(), ipAddress, false, e.getMessage());
            throw e;
        }
    }

    public AuthResponse register(RegisterRequest request, String ipAddress) {
        // Vérifier si l'email existe déjà
        if (userDao.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        // Récupérer le rôle USER par défaut
        Role roleUser = roleDao.findByLibelle("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Rôle ROLE_USER non trouvé"));

        // Créer l'utilisateur
        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleUser);
        user.setActive(true);

        User savedUser = userDao.save(user);

        // Générer le token
        String token = jwtUtils.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getRole().getLibelle());

        // Log connexion réussie après inscription
        logConnexionService.logConnexion(request.getEmail(), ipAddress, true, "Inscription");

        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getPrenom());
        } catch (RuntimeException e) {
            // Ne bloque pas l'inscription si l'email ne peut pas etre envoye.
            log.warn("Inscription reussie mais echec d'envoi de l'email de bienvenue pour {}", savedUser.getEmail(), e);
        }

        return new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getNom(),
            savedUser.getPrenom(),
            savedUser.getRole().getLibelle()
        );
    }
}

