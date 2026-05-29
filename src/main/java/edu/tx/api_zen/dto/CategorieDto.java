package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CategorieDto {
    @Schema(example = "1")
    private Integer idCategorie;
    @Schema(example = "Bien-etre")
    private String libelle;
    @Schema(example = "Articles sur le bien-etre et la sante mentale")
    private String description;

    public Integer getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Integer idCategorie) { this.idCategorie = idCategorie; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

