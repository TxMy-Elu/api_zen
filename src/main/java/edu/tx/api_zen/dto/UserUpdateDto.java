package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserUpdateDto {
    @Schema(example = "Dupont")
    private String nom;
    @Schema(example = "Camille")
    private String prenom;
    @Schema(example = "utilisateur.maj@example.com")
    private String email;
    @Schema(example = "MotDePasseMaj123!")
    private String password;
    @Schema(example = "2")
    private Integer roleId;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
}

