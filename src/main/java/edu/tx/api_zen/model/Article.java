package edu.tx.api_zen.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_article")
    private Integer idArticle;

    @Column(name = "titre", length = 200)
    private String titre;

    @Column(name = "contenu", columnDefinition = "TEXT")
    private String contenu; // texte markdown ou HTML

    @Column(name = "type_media", length = 50)
    private String typeMedia; // "text", "image", "video", "file"

    @Column(name = "media_url", length = 1024)
    private String mediaUrl; // URL ou chemin vers le fichier

    @Column(name = "date_publication")
    private Instant datePublication;

    @Column(name = "date_modification")
    private Instant dateModification;

    @Column(name = "est_publie")
    private Boolean estPublie = false;

    @ManyToOne
    @JoinColumn(name = "id_categorie")
    private Categorie categorie;

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

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
}
