package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ConsulterCreateDto {
    
    @NotNull(message = "L'id de l'utilisateur est requis")
    @Schema(example = "1")
    private Integer idUtilisateur;
    
    @NotNull(message = "L'id de l'article est requis")
    @Schema(example = "2")
    private Integer idArticle;

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public Integer getIdArticle() { return idArticle; }
    public void setIdArticle(Integer idArticle) { this.idArticle = idArticle; }
}

