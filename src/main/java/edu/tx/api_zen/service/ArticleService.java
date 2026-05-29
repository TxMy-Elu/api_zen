package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.ArticleDao;
import edu.tx.api_zen.dao.CategorieDao;
import edu.tx.api_zen.dao.ConsulterDao;
import edu.tx.api_zen.dto.ArticleCreateDto;
import edu.tx.api_zen.dto.ArticleDto;
import edu.tx.api_zen.mapper.ArticleMapper;
import edu.tx.api_zen.model.Article;
import edu.tx.api_zen.model.Categorie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleDao articleDao;
    private final CategorieDao categorieDao;
    private final ConsulterDao consulterDao;

    public List<ArticleDto> listAll() {
        return articleDao.findAll().stream().map(ArticleMapper::toDto).collect(Collectors.toList());
    }

    public List<ArticleDto> listPublic() {
        return articleDao.findByEstPublieTrue().stream().map(ArticleMapper::toDto).collect(Collectors.toList());
    }

    public Optional<ArticleDto> getById(Integer id) {
        return articleDao.findById(id).map(ArticleMapper::toDto);
    }

    public ArticleDto create(ArticleCreateDto dto) {
        if (dto.getTypeMedia() != null && !dto.getTypeMedia().equalsIgnoreCase("text") && (dto.getMediaUrl() == null || dto.getMediaUrl().isBlank())) {
            throw new IllegalArgumentException("mediaUrl is required for non-text articles");
        }
        Article a = ArticleMapper.toEntity(dto);
        a.setDatePublication(Instant.now());
        if (dto.getIdCategorie() != null) {
            Categorie cat = categorieDao.findById(dto.getIdCategorie())
                    .orElseThrow(() -> new IllegalArgumentException("Categorie not found"));
            a.setCategorie(cat);
        }
        Article saved = articleDao.save(a);
        return ArticleMapper.toDto(saved);
    }

    public ArticleDto update(Integer id, ArticleCreateDto dto) {
        Optional<Article> opt = articleDao.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Article not found");
        Article a = opt.get();
        if (dto.getTitre() != null) a.setTitre(dto.getTitre());
        if (dto.getContenu() != null) a.setContenu(dto.getContenu());
        if (dto.getTypeMedia() != null) a.setTypeMedia(dto.getTypeMedia());
        a.setMediaUrl(dto.getMediaUrl()); // null = retrait explicite du fichier
        if (dto.getEstPublie() != null) a.setEstPublie(dto.getEstPublie());
        if (dto.getIdCategorie() != null) {
            Categorie cat = categorieDao.findById(dto.getIdCategorie())
                    .orElseThrow(() -> new IllegalArgumentException("Categorie not found"));
            a.setCategorie(cat);
        }
        a.setDateModification(Instant.now());
        Article saved = articleDao.save(a);
        return ArticleMapper.toDto(saved);
    }

    @Transactional
    public void delete(Integer id) {
        if (!articleDao.existsById(id)) {
            throw new IllegalArgumentException("Article not found");
        }
        consulterDao.deleteByArticleIdArticle(id);
        articleDao.deleteById(id);
    }
}

