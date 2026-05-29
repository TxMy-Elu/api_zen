package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}

