package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.PasswordResetTokenDao;
import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.model.PasswordResetToken;
import edu.tx.api_zen.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserDao userDao;
    private final PasswordResetTokenDao tokenDao;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userDao.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        tokenDao.findByUserAndUsedFalse(user).ifPresent(existingToken -> {
            existingToken.setUsed(true);
            tokenDao.save(existingToken);
        });

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        resetToken.setUsed(false);

        tokenDao.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public boolean validateToken(String token) {
        PasswordResetToken resetToken = tokenDao.findByToken(token).orElse(null);

        if (resetToken == null) {
            return false;
        }

        return !resetToken.getUsed() && !resetToken.isExpired();
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenDao.findByToken(token).orElse(null);

        if (resetToken == null || resetToken.getUsed() || resetToken.isExpired()) {
            return false;
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);

        resetToken.setUsed(true);
        tokenDao.save(resetToken);

        try {
            emailService.sendPasswordResetConfirmationEmail(user.getEmail());
        } catch (IllegalStateException e) {
            log.warn("Reset password effectue mais email de confirmation non envoye pour {}", user.getEmail(), e);
        }

        return true;
    }
}

