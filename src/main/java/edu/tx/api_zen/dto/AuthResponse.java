package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthResponse {
    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.demo.signature")
    private String token;
    @Schema(example = "Bearer")
    private String type = "Bearer";
    @Schema(example = "12")
    private Integer userId;
    @Schema(example = "camille.martin@example.com")
    private String email;
    @Schema(example = "Martin")
    private String nom;
    @Schema(example = "Camille")
    private String prenom;
    @Schema(example = "USER")
    private String role;

    public AuthResponse(String token, Integer userId, String email, String nom, String prenom, String role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

