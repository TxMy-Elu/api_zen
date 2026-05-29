package edu.tx.api_zen.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final DateTimeFormatter LOGIN_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@cesizen.fr}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = buildResetLink(token);
        String plainTextBody = buildPlainTextBody(resetLink);

        try {
            String htmlBody = buildHtmlBody(resetLink);
            sendEmail(toEmail, "CESIZen - Reinitialisation de votre mot de passe", plainTextBody, htmlBody);
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("Impossible de construire l'email de reinitialisation.", e);
        }
    }

    public void sendWelcomeEmail(String toEmail, String firstName) {
        String loginLink = buildLoginLink();
        String plainTextBody = buildWelcomePlainTextBody(firstName, loginLink);

        try {
            String htmlBody = buildWelcomeHtmlBody(firstName, loginLink);
            sendEmail(toEmail, "CESIZen - Bienvenue", plainTextBody, htmlBody);
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("Impossible de construire l'email de bienvenue.", e);
        }
    }

    public void sendPasswordResetConfirmationEmail(String toEmail) {
        String plainTextBody = buildPasswordResetConfirmationPlainTextBody();

        try {
            String htmlBody = buildPasswordResetConfirmationHtmlBody();
            sendEmail(toEmail, "CESIZen - Votre mot de passe a ete reinitialise", plainTextBody, htmlBody);
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("Impossible de construire l'email de confirmation de reinitialisation.", e);
        }
    }

    public void sendLoginConnectionEmail(String toEmail, String firstName, String ipAddress) {
        String displayName = (firstName == null || firstName.isBlank()) ? "utilisateur" : firstName;
        String loginDate = LOGIN_DATE_FORMATTER.format(Instant.now());
        String plainTextBody = buildLoginConnectionPlainTextBody(displayName, ipAddress, loginDate);

        try {
            String htmlBody = buildLoginConnectionHtmlBody(displayName, ipAddress, loginDate);
            sendEmail(toEmail, "CESIZen - Nouvelle connexion detectee", plainTextBody, htmlBody);
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("Impossible de construire l'email de notification de connexion.", e);
        }
    }

    private String buildResetLink(String token) {
        return UriComponentsBuilder
            .fromUriString(frontendUrl)
            .path("/auth/reset-mot-de-passe")
            .queryParam("token", token)
            .build()
            .toUriString();
    }

    private String buildLoginLink() {
        return UriComponentsBuilder
            .fromUriString(frontendUrl)
            .path("/auth/connexion")
            .build()
            .toUriString();
    }

    private String buildPlainTextBody(String resetLink) {
        return "Bonjour,\n\n"
            + "Vous avez demande la reinitialisation de votre mot de passe.\n\n"
            + "Cliquez sur le lien suivant pour definir un nouveau mot de passe :\n"
            + resetLink + "\n\n"
            + "Ce lien est valable pendant 1 heure.\n\n"
            + "Si vous n'etes pas a l'origine de cette demande, ignorez cet email.\n\n"
            + "L'equipe CESIZen";
    }

    private String buildHtmlBody(String resetLink) throws IOException {
        ClassPathResource template = new ClassPathResource("templates/emails/password-reset.html");
        String html = new String(template.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return html.replace("{{resetLink}}", resetLink);
    }

    private String buildWelcomePlainTextBody(String firstName, String loginLink) {
        return "Bonjour " + firstName + ",\n\n"
            + "Votre inscription sur CESIZen est confirmee.\n"
            + "Vous pouvez maintenant vous connecter via le lien suivant :\n"
            + loginLink + "\n\n"
            + "A bientot,\n"
            + "L'equipe CESIZen";
    }

    private String buildWelcomeHtmlBody(String firstName, String loginLink) throws IOException {
        ClassPathResource template = new ClassPathResource("templates/emails/welcome-registration.html");
        String html = new String(template.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return html
            .replace("{{firstName}}", firstName)
            .replace("{{loginLink}}", loginLink);
    }

    private String buildPasswordResetConfirmationPlainTextBody() {
        return "Bonjour,\n\n"
            + "Votre mot de passe a ete reinitialise avec succes.\n\n"
            + "Si vous etes a l'origine de cette action, aucune action supplementaire n'est necessaire.\n"
            + "Si ce n'est pas vous, changez immediatement votre mot de passe et contactez le support.\n\n"
            + "L'equipe CESIZen";
    }

    private String buildPasswordResetConfirmationHtmlBody() throws IOException {
        ClassPathResource template = new ClassPathResource("templates/emails/password-reset-confirmation.html");
        return new String(template.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String buildLoginConnectionPlainTextBody(String firstName, String ipAddress, String loginDate) {
        return "Bonjour " + firstName + ",\n\n"
            + "Une connexion a votre compte vient d'etre detectee.\n"
            + "Date: " + loginDate + "\n"
            + "Adresse IP: " + ipAddress + "\n\n"
            + "Si c'est bien vous, aucune action n'est necessaire.\n"
            + "Si ce n'est pas vous, changez immediatement votre mot de passe.\n\n"
            + "L'equipe CESIZen";
    }

    private String buildLoginConnectionHtmlBody(String firstName, String ipAddress, String loginDate) throws IOException {
        ClassPathResource template = new ClassPathResource("templates/emails/login-connection.html");
        String html = new String(template.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return html
            .replace("{{firstName}}", firstName)
            .replace("{{ipAddress}}", ipAddress)
            .replace("{{loginDate}}", loginDate);
    }

    private void sendEmail(String toEmail, String subject, String plainTextBody, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(plainTextBody, htmlBody);
        mailSender.send(message);
    }
}

