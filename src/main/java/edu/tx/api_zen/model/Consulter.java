package edu.tx.api_zen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "consulter")
public class Consulter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulter")
    private Integer idConsulter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_article")
    private Article article;

    @Column(name = "viewed_at")
    private Instant viewedAt;

    @PrePersist
    public void prePersist() {
        if (viewedAt == null) {
            viewedAt = Instant.now();
        }
    }
}

