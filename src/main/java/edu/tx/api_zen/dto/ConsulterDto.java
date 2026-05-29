package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class ConsulterDto {
    @Schema(example = "15")
    private Integer idConsulter;
    @Schema(example = "1")
    private Integer idUtilisateur;
    @Schema(example = "Camille Martin")
    private String nomUtilisateur;
    @Schema(example = "2")
    private Integer idArticle;
    @Schema(example = "Mon nouvel article")
    private String titreArticle;
    @Schema(example = "2026-03-30T10:15:30Z")
    private Instant viewedAt;

    public Integer getIdConsulter() { return idConsulter; }
    public void setIdConsulter(Integer idConsulter) { this.idConsulter = idConsulter; }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public Integer getIdArticle() { return idArticle; }
    public void setIdArticle(Integer idArticle) { this.idArticle = idArticle; }

    public String getTitreArticle() { return titreArticle; }
    public void setTitreArticle(String titreArticle) { this.titreArticle = titreArticle; }

    public Instant getViewedAt() { return viewedAt; }
    public void setViewedAt(Instant viewedAt) { this.viewedAt = viewedAt; }
}

