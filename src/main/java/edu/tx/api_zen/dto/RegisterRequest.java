package edu.tx.api_zen.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class RegisterRequest {
    
    @NotBlank(message = "Le nom est requis")
    @Schema(example = "Nouveau")
    private String nom;
    
    @NotBlank(message = "Le prénom est requis")
    @Schema(example = "Utilisateur")
    private String prenom;
    
    @NotBlank(message = "L'email est requis")
    @Email(message = "Email invalide")
    @Schema(example = "utilisateur@example.com")
    private String email;
    
    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Pattern(regexp = "[^0-9]*[0-9].*", message = "Le mot de passe doit contenir au moins un chiffre")
    @Pattern(regexp = "[^!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*", message = "Le mot de passe doit contenir au moins un caractère spécial")
    @Schema(example = "MotDePasse123!")
    private String password;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

