package edu.tx.api_zen.mapper;

import edu.tx.api_zen.dto.ConsulterDto;
import edu.tx.api_zen.model.Consulter;

public class ConsulterMapper {

    public static ConsulterDto toDto(Consulter c) {
        if (c == null) return null;
        ConsulterDto dto = new ConsulterDto();
        dto.setIdConsulter(c.getIdConsulter());
        dto.setIdUtilisateur(c.getUser().getId());
        dto.setNomUtilisateur(c.getUser().getPrenom() + " " + c.getUser().getNom());
        dto.setIdArticle(c.getArticle().getIdArticle());
        dto.setTitreArticle(c.getArticle().getTitre());
        dto.setViewedAt(c.getViewedAt());
        return dto;
    }
}

