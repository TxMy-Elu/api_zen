package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.ArticleDao;
import edu.tx.api_zen.dao.CategorieDao;
import edu.tx.api_zen.dto.CategorieCreateDto;
import edu.tx.api_zen.dto.CategorieDto;
import edu.tx.api_zen.mapper.CategorieMapper;
import edu.tx.api_zen.model.Article;
import edu.tx.api_zen.model.Categorie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieDao categorieDao;
    private final ArticleDao articleDao;

    public List<CategorieDto> listAll() {
        return categorieDao.findAll().stream().map(CategorieMapper::toDto).collect(Collectors.toList());
    }

    public Optional<CategorieDto> getById(Integer id) {
        return categorieDao.findById(id).map(CategorieMapper::toDto);
    }

    public CategorieDto create(CategorieCreateDto dto) {
        Categorie c = CategorieMapper.toEntity(dto);
        Categorie saved = categorieDao.save(c);
        return CategorieMapper.toDto(saved);
    }

    public CategorieDto update(Integer id, CategorieCreateDto dto) {
        Optional<Categorie> opt = categorieDao.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Categorie not found");
        Categorie c = opt.get();
        c.setLibelle(dto.getLibelle());
        c.setDescription(dto.getDescription());
        Categorie saved = categorieDao.save(c);
        return CategorieMapper.toDto(saved);
    }

    @Transactional
    public void delete(Integer id) {
        Categorie toDelete = categorieDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categorie not found"));

        if ("Général".equalsIgnoreCase(toDelete.getLibelle())) {
            throw new IllegalStateException("La catégorie 'Général' ne peut pas être supprimée.");
        }

        long articleCount = articleDao.countByCategorieIdCategorie(id);
        if (articleCount > 0) {
            Categorie general = categorieDao.findByLibelle("Général")
                    .orElseGet(() -> {
                        Categorie c = new Categorie();
                        c.setLibelle("Général");
                        c.setDescription("Catégorie générale");
                        return categorieDao.save(c);
                    });

            List<Article> articles = articleDao.findByCategorieIdCategorie(id);
            for (Article a : articles) {
                a.setCategorie(general);
                articleDao.save(a);
            }
        }

        categorieDao.deleteById(id);
    }
}

