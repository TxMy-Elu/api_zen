package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ArticleCreateDto {
    @NotBlank
    @Schema(example = "Mon nouvel article")
    private String titre;
    @Schema(example = "Contenu de l'article...")
    private String contenu;
    @NotBlank
    @Schema(example = "text")
    private String typeMedia;
    @Schema(example = "https://example.com/media.pdf", nullable = true)
    private String mediaUrl;
    @Schema(example = "false")
    private Boolean estPublie = false;
    @Schema(example = "1")
    private Integer idCategorie;

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getTypeMedia() { return typeMedia; }
    public void setTypeMedia(String typeMedia) { this.typeMedia = typeMedia; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public Boolean getEstPublie() { return estPublie; }
    public void setEstPublie(Boolean estPublie) { this.estPublie = estPublie; }
    public Integer getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Integer idCategorie) { this.idCategorie = idCategorie; }
}

