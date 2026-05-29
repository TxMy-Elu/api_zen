package edu.tx.api_zen.mapper;

import edu.tx.api_zen.dto.ExercerDto;
import edu.tx.api_zen.model.Exercer;

public class ExercerMapper {

    public static ExercerDto toDto(Exercer e) {
        if (e == null) return null;
        ExercerDto dto = new ExercerDto();
        dto.setIdExercer(e.getIdExercer());
        if (e.getUser() != null) dto.setUser(UserMapper.toDto(e.getUser()));
        if (e.getExercice() != null) dto.setExercice(ExerciceMapper.toDto(e.getExercice()));
        dto.setCompletedAt(e.getCompletedAt());
        return dto;
    }
}
