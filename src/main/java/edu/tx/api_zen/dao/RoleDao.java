package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {
    Optional<Role> findByLibelle(String libelle);
}

