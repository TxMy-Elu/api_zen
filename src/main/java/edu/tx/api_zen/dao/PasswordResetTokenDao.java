package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.PasswordResetToken;
import edu.tx.api_zen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenDao extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);

    void deleteByUser(User user);
}

