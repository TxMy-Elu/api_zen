package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class LogActiviteCreateDto {
    
    @NotBlank(message = "Le type d'action est requis")
    @Schema(example = "CREATE")
    private String typeAction;
    
    @NotBlank(message = "La table concernée est requise")
    @Schema(example = "article")
    private String tableConcernee;
    
    @Schema(example = "1")
    private Integer idEnregistrement;
    @Schema(example = "Creation d'un nouvel article")
    private String details;
    // RFC 5737 : plage réservée à la documentation, jamais routable
    @Schema(example = "203.0.113.100")
    private String adresseIp;
    @Schema(example = "1")
    private Integer idUtilisateur;

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
}

