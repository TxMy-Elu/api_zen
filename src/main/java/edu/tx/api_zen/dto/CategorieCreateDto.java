package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class CategorieCreateDto {
    @NotBlank
    @Schema(example = "Bien-etre")
    private String libelle;
    @Schema(example = "Articles sur le bien-etre et la sante mentale")
    private String description;

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

