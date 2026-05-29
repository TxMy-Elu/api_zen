package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieDao extends JpaRepository<Categorie, Integer> {
    Optional<Categorie> findByLibelle(String libelle);
}

