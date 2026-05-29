package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class ExercerDto {
    @Schema(example = "8")
    private Integer idExercer;
    private UserDto user;
    private ExerciceDto exercice;
    @Schema(example = "2026-03-30T10:15:30Z")
    private Instant completedAt;

    public Integer getIdExercer() { return idExercer; }
    public void setIdExercer(Integer idExercer) { this.idExercer = idExercer; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public ExerciceDto getExercice() { return exercice; }
    public void setExercice(ExerciceDto exercice) { this.exercice = exercice; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
