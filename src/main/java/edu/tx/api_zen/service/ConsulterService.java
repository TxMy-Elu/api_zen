package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.ArticleDao;
import edu.tx.api_zen.dao.ConsulterDao;
import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.dto.ConsulterCreateDto;
import edu.tx.api_zen.dto.ConsulterDto;
import edu.tx.api_zen.mapper.ConsulterMapper;
import edu.tx.api_zen.model.Article;
import edu.tx.api_zen.model.Consulter;
import edu.tx.api_zen.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsulterService {

    private final ConsulterDao consulterDao;
    private final UserDao userDao;
    private final ArticleDao articleDao;

    // Liste toutes les consultations
    public List<ConsulterDto> listAll() {
        return consulterDao.findAll().stream()
                .map(ConsulterMapper::toDto)
                .collect(Collectors.toList());
    }

    // Consultations par utilisateur
    public List<ConsulterDto> getByUserId(Integer userId) {
        return consulterDao.findByUserIdOrderByViewedAtDesc(userId).stream()
                .map(ConsulterMapper::toDto)
                .collect(Collectors.toList());
    }

    // Consultations par article
    public List<ConsulterDto> getByArticleId(Integer articleId) {
        return consulterDao.findByArticleIdArticleOrderByViewedAtDesc(articleId).stream()
                .map(ConsulterMapper::toDto)
                .collect(Collectors.toList());
    }

    // Enregistrer une consultation (marquer un article comme vu)
    public ConsulterDto create(ConsulterCreateDto dto) {
        User user = userDao.findById(dto.getIdUtilisateur())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getIdUtilisateur()));
        
        Article article = articleDao.findById(dto.getIdArticle())
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + dto.getIdArticle()));

        Consulter consulter = new Consulter();
        consulter.setUser(user);
        consulter.setArticle(article);
        consulter.setViewedAt(Instant.now());

        Consulter saved = consulterDao.save(consulter);
        return ConsulterMapper.toDto(saved);
    }

    // Nombre de vues d'un article
    public long countViewsByArticle(Integer articleId) {
        return consulterDao.countByArticleIdArticle(articleId);
    }

    // Nombre d'articles consultés par un utilisateur
    public long countArticlesByUser(Integer userId) {
        return consulterDao.countByUserId(userId);
    }

    // Supprimer une consultation
    public void delete(Integer id) {
        consulterDao.deleteById(id);
    }
}

