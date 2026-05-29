package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciceDao extends JpaRepository<Exercice, Integer> {
}

