package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class LogActiviteDto {
    @Schema(example = "27")
    private Integer idLogActivite;
    @Schema(example = "2026-03-30T10:15:30Z")
    private Instant dateAction;
    @Schema(example = "UPDATE")
    private String typeAction;
    @Schema(example = "user")
    private String tableConcernee;
    @Schema(example = "12")
    private Integer idEnregistrement;
    @Schema(example = "Mise a jour du profil utilisateur")
    private String details;
    @Schema(example = "10.0.0.12")
    private String adresseIp;
    @Schema(example = "1")
    private Integer idUtilisateur;
    @Schema(example = "Camille Martin")
    private String nomUtilisateur;

    public Integer getIdLogActivite() { return idLogActivite; }
    public void setIdLogActivite(Integer idLogActivite) { this.idLogActivite = idLogActivite; }

    public Instant getDateAction() { return dateAction; }
    public void setDateAction(Instant dateAction) { this.dateAction = dateAction; }

    public String getTypeAction() { return typeAction; }
    public void setTypeAction(String typeAction) { this.typeAction = typeAction; }

    public String getTableConcernee() { return tableConcernee; }
    public void setTableConcernee(String tableConcernee) { this.tableConcernee = tableConcernee; }

    public Integer getIdEnregistrement() { return idEnregistrement; }
    public void setIdEnregistrement(Integer idEnregistrement) { this.idEnregistrement = idEnregistrement; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getAdresseIp() { return adresseIp; }
    public void setAdresseIp(String adresseIp) { this.adresseIp = adresseIp; }

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
}

