package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class ArticleDto {
    @Schema(example = "2")
    private Integer idArticle;
    @Schema(example = "Mon nouvel article")
    private String titre;
    @Schema(example = "Contenu de l'article...")
    private String contenu;
    @Schema(example = "text")
    private String typeMedia;
    @Schema(example = "/uploads/1743351234567_guide.pdf")
    private String mediaUrl;
    @Schema(example = "2026-03-30T10:15:30Z")
    private Instant datePublication;
    @Schema(example = "2026-03-30T11:20:00Z")
    private Instant dateModification;
    @Schema(example = "true")
    private Boolean estPublie;
    @Schema(example = "1")
    private Integer idCategorie;
    @Schema(example = "Bien-etre")
    private String categorieLibelle;

    public Integer getIdArticle() { return idArticle; }
    public void setIdArticle(Integer idArticle) { this.idArticle = idArticle; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getTypeMedia() { return typeMedia; }
    public void setTypeMedia(String typeMedia) { this.typeMedia = typeMedia; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public Instant getDatePublication() { return datePublication; }
    public void setDatePublication(Instant datePublication) { this.datePublication = datePublication; }
    public Instant getDateModification() { return dateModification; }
    public void setDateModification(Instant dateModification) { this.dateModification = dateModification; }
    public Boolean getEstPublie() { return estPublie; }
    public void setEstPublie(Boolean estPublie) { this.estPublie = estPublie; }
    public Integer getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Integer idCategorie) { this.idCategorie = idCategorie; }
    public String getCategorieLibelle() { return categorieLibelle; }
    public void setCategorieLibelle(String categorieLibelle) { this.categorieLibelle = categorieLibelle; }
}

