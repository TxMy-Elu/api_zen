package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ExerciceDto {
    @Schema(example = "1")
    private Integer idExercice;
    @Schema(example = "Respiration profonde lente")
    private String nom;
    @Schema(example = "6")
    private Integer dureeInspiration;
    @Schema(example = "2")
    private Integer dureeApnee;
    @Schema(example = "6")
    private Integer dureeExpiration;
    @Schema(example = "120")
    private Integer dureeSession;
    @Schema(example = "Inspiration lente, petite apnee, expiration longue.")
    private String description;

    public Integer getIdExercice() { return idExercice; }
    public void setIdExercice(Integer idExercice) { this.idExercice = idExercice; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Integer getDureeInspiration() { return dureeInspiration; }
    public void setDureeInspiration(Integer dureeInspiration) { this.dureeInspiration = dureeInspiration; }
    public Integer getDureeApnee() { return dureeApnee; }
    public void setDureeApnee(Integer dureeApnee) { this.dureeApnee = dureeApnee; }
    public Integer getDureeExpiration() { return dureeExpiration; }
    public void setDureeExpiration(Integer dureeExpiration) { this.dureeExpiration = dureeExpiration; }
    public Integer getDureeSession() { return dureeSession; }
    public void setDureeSession(Integer dureeSession) { this.dureeSession = dureeSession; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

