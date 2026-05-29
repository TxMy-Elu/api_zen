package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class UserDto {
    @Schema(example = "12")
    private Integer id;
    @Schema(example = "Martin")
    private String nom;
    @Schema(example = "Camille")
    private String prenom;
    @Schema(example = "camille.martin@example.com")
    private String email;
    @Schema(example = "true")
    private Boolean active;
    @Schema(example = "2026-03-30T10:15:30Z")
    private Instant creationDate;
    @Schema(example = "2")
    private Integer roleId;
    @Schema(example = "USER")
    private String roleLibelle;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Instant getCreationDate() { return creationDate; }
    public void setCreationDate(Instant creationDate) { this.creationDate = creationDate; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    public String getRoleLibelle() { return roleLibelle; }
    public void setRoleLibelle(String roleLibelle) { this.roleLibelle = roleLibelle; }
}

