package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.Exercer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercerDao extends JpaRepository<Exercer, Integer> {
    // Recherches utiles pour lister les participations d'un utilisateur ou d'un exercice
    List<Exercer> findByUserId(Integer userId);
    List<Exercer> findByExercice_IdExercice(Integer exerciceId);
}
