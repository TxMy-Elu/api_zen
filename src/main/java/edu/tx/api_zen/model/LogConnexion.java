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
@Table(name = "log_connexion")
public class LogConnexion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log_connexion")
    private Integer idLogConnexion;

    @Column(name = "date_connexion")
    private Instant dateConnexion;

    @Column(name = "adresse_ip", length = 45)
    private String adresseIp;

    @Column(name = "reussite")
    private Boolean reussite;

    @Column(name = "motif_echec", length = 255)
    private String motifEchec;

    @Column(name = "email_tente", length = 255)
    private String emailTente;

    @PrePersist
    public void prePersist() {
        if (dateConnexion == null) {
            dateConnexion = Instant.now();
        }
    }
}

