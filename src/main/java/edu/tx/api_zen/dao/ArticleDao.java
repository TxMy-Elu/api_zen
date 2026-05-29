package edu.tx.api_zen.dao;

import edu.tx.api_zen.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleDao extends JpaRepository<Article, Integer> {
    List<Article> findByEstPublieTrue();
    List<Article> findByCategorieIdCategorie(Integer idCategorie);
    long countByCategorieIdCategorie(Integer idCategorie);
}

