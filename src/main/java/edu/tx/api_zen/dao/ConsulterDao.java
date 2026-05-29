package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.Consulter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsulterDao extends JpaRepository<Consulter, Integer> {
    
    // Trouver les consultations par utilisateur
    List<Consulter> findByUserIdOrderByViewedAtDesc(Integer userId);
    
    // Trouver les consultations par article
    List<Consulter> findByArticleIdArticleOrderByViewedAtDesc(Integer articleId);
    
    // Vérifier si un utilisateur a déjà consulté un article
    boolean existsByUserIdAndArticleIdArticle(Integer userId, Integer articleId);
    
    // Compter le nombre de vues d'un article
    long countByArticleIdArticle(Integer articleId);
    
    // Compter le nombre d'articles consultés par un utilisateur
    long countByUserId(Integer userId);

    // Supprimer toutes les consultations liées à un article (avant suppression article)
    void deleteByArticleIdArticle(Integer articleId);
}

