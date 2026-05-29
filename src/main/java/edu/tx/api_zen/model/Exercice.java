package edu.tx.api_zen.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exercice_respiration")
public class Exercice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercice")
    private Integer idExercice;

    @Column(name = "nom", length = 100)
    private String nom;

    @Column(name = "duree_inspiration")
    private Integer dureeInspiration;

    @Column(name = "duree_apnee")
    private Integer dureeApnee;

    @Column(name = "duree_expiration")
    private Integer dureeExpiration;

    @Column(name = "duree_session")
    private Integer dureeSession;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}
