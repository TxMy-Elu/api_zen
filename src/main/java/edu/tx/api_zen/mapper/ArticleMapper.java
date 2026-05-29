package edu.tx.api_zen.mapper;

import edu.tx.api_zen.dto.ArticleCreateDto;
import edu.tx.api_zen.dto.ArticleDto;
import edu.tx.api_zen.model.Article;

public class ArticleMapper {

    public static ArticleDto toDto(Article a) {
        if (a == null) return null;
        ArticleDto dto = new ArticleDto();
        dto.setIdArticle(a.getIdArticle());
        dto.setTitre(a.getTitre());
        dto.setContenu(a.getContenu());
        dto.setTypeMedia(a.getTypeMedia());
        dto.setMediaUrl(a.getMediaUrl());
        dto.setDatePublication(a.getDatePublication());
        dto.setDateModification(a.getDateModification());
        dto.setEstPublie(a.getEstPublie());
        if (a.getCategorie() != null) {
            dto.setIdCategorie(a.getCategorie().getIdCategorie());
            dto.setCategorieLibelle(a.getCategorie().getLibelle());
        }
        return dto;
    }

    public static Article toEntity(ArticleCreateDto dto) {
        if (dto == null) return null;
        Article a = new Article();
        a.setTitre(dto.getTitre());
        a.setContenu(dto.getContenu());
        a.setTypeMedia(dto.getTypeMedia());
        a.setMediaUrl(dto.getMediaUrl());
        a.setEstPublie(dto.getEstPublie() == null ? false : dto.getEstPublie());
        return a;
    }
}

