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
@Table(name = "exercer")
public class Exercer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercer")
    private Integer idExercer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_exercice")
    private Exercice exercice;

    @Column(name = "completed_at")
    private Instant completedAt;

}
