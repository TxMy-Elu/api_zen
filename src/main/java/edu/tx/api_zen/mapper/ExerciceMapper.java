package edu.tx.api_zen.mapper;

import edu.tx.api_zen.dto.ExerciceCreateDto;
import edu.tx.api_zen.dto.ExerciceDto;
import edu.tx.api_zen.model.Exercice;

public class ExerciceMapper {

    public static ExerciceDto toDto(Exercice e) {
        if (e == null) return null;
        ExerciceDto dto = new ExerciceDto();
        dto.setIdExercice(e.getIdExercice());
        dto.setNom(e.getNom());
        dto.setDureeInspiration(e.getDureeInspiration());
        dto.setDureeApnee(e.getDureeApnee());
        dto.setDureeExpiration(e.getDureeExpiration());
        dto.setDureeSession(e.getDureeSession());
        dto.setDescription(e.getDescription());
        return dto;
    }

    public static Exercice toEntity(ExerciceCreateDto dto) {
        if (dto == null) return null;
        Exercice e = new Exercice();
        e.setNom(dto.getNom());
        e.setDureeInspiration(dto.getDureeInspiration());
        e.setDureeApnee(dto.getDureeApnee());
        e.setDureeExpiration(dto.getDureeExpiration());
        e.setDureeSession(dto.getDureeSession());
        e.setDescription(dto.getDescription());
        return e;
    }
}

