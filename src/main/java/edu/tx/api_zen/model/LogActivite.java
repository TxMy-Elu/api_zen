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
@Table(name = "log_activite")
public class LogActivite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log_activite")
    private Integer idLogActivite;

    @Column(name = "date_action")
    private Instant dateAction;

    @Column(name = "type_action", length = 50)
    private String typeAction; // CREATE, UPDATE, DELETE, READ

    @Column(name = "table_concernee", length = 50)
    private String tableConcernee; // article, user, exercice, etc.

    @Column(name = "id_enregistrement")
    private Integer idEnregistrement; // ID de l'élément concerné

    @Column(name = "details", columnDefinition = "TEXT")
    private String details; // Description détaillée de l'action

    @Column(name = "adresse_ip", length = 45)
    private String adresseIp;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private User user; // Utilisateur qui a effectué l'action (nullable pour actions système)

    @PrePersist
    public void prePersist() {
        if (dateAction == null) {
            dateAction = Instant.now();
        }
    }
}

