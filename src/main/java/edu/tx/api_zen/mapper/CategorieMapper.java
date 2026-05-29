package edu.tx.api_zen.mapper;

import edu.tx.api_zen.dto.CategorieCreateDto;
import edu.tx.api_zen.dto.CategorieDto;
import edu.tx.api_zen.model.Categorie;

public class CategorieMapper {

    public static CategorieDto toDto(Categorie c) {
        if (c == null) return null;
        CategorieDto dto = new CategorieDto();
        dto.setIdCategorie(c.getIdCategorie());
        dto.setLibelle(c.getLibelle());
        dto.setDescription(c.getDescription());
        return dto;
    }

    public static Categorie toEntity(CategorieCreateDto dto) {
        if (dto == null) return null;
        Categorie c = new Categorie();
        c.setLibelle(dto.getLibelle());
        c.setDescription(dto.getDescription());
        return c;
    }
}

