package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.LogActivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface LogActiviteDao extends JpaRepository<LogActivite, Integer> {
    
    // Par utilisateur
    List<LogActivite> findByUserIdOrderByDateActionDesc(Integer userId);
    
    // Par table concernée
    List<LogActivite> findByTableConcerneeOrderByDateActionDesc(String tableConcernee);
    
    // Par type d'action
    List<LogActivite> findByTypeActionOrderByDateActionDesc(String typeAction);
    
    // Par date (entre deux dates)
    List<LogActivite> findByDateActionBetweenOrderByDateActionDesc(Instant start, Instant end);
    
    // Par table et ID enregistrement (historique d'un élément spécifique)
    List<LogActivite> findByTableConcerneeAndIdEnregistrementOrderByDateActionDesc(String tableConcernee, Integer idEnregistrement);
    
    // Dernières actions (top N)
    List<LogActivite> findTop50ByOrderByDateActionDesc();

    // Anonymisation RGPD : détacher les logs d'un utilisateur supprimé
    @Modifying
    @Query("UPDATE LogActivite l SET l.user = null WHERE l.user.id = :userId")
    void detachUser(@Param("userId") Integer userId);
}

