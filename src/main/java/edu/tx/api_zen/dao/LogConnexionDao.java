package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.LogConnexion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogConnexionDao extends JpaRepository<LogConnexion, Integer> {
    
    List<LogConnexion> findByEmailTenteOrderByDateConnexionDesc(String email);
    
    List<LogConnexion> findByReussiteOrderByDateConnexionDesc(Boolean reussite);
    
    List<LogConnexion> findTop50ByOrderByDateConnexionDesc();
    
    List<LogConnexion> findByAdresseIpOrderByDateConnexionDesc(String adresseIp);

    // Anonymisation RGPD : remplacer l'email dans les logs de connexion
    @Modifying
    @Query("UPDATE LogConnexion l SET l.emailTente = :anonymEmail WHERE l.emailTente = :realEmail")
    void anonymizeEmail(@Param("realEmail") String realEmail, @Param("anonymEmail") String anonymEmail);
}

