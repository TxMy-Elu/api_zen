package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class LogConnexionDto {
    @Schema(example = "41")
    private Integer idLogConnexion;
    @Schema(example = "2026-03-30T10:15:30Z")
    private Instant dateConnexion;
    @Schema(example = "203.0.113.42")
    private String adresseIp;
    @Schema(example = "true")
    private Boolean reussite;
    @Schema(example = "Email ou mot de passe incorrect")
    private String motifEchec;
    @Schema(example = "utilisateur@example.com")
    private String emailTente;

    public Integer getIdLogConnexion() { return idLogConnexion; }
    public void setIdLogConnexion(Integer idLogConnexion) { this.idLogConnexion = idLogConnexion; }
    public Instant getDateConnexion() { return dateConnexion; }
    public void setDateConnexion(Instant dateConnexion) { this.dateConnexion = dateConnexion; }
    public String getAdresseIp() { return adresseIp; }
    public void setAdresseIp(String adresseIp) { this.adresseIp = adresseIp; }
    public Boolean getReussite() { return reussite; }
    public void setReussite(Boolean reussite) { this.reussite = reussite; }
    public String getMotifEchec() { return motifEchec; }
    public void setMotifEchec(String motifEchec) { this.motifEchec = motifEchec; }
    public String getEmailTente() { return emailTente; }
    public void setEmailTente(String emailTente) { this.emailTente = emailTente; }
}

