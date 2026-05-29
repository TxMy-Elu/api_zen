package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class ExercerCreateDto {
    @NotNull
    @Schema(example = "2")
    private Integer userId;
    @NotNull
    @Schema(example = "1")
    private Integer exerciceId;
    @Schema(example = "2026-03-04T10:15:30Z")
    private String completedAt; // ISO-8601 optional

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getExerciceId() { return exerciceId; }
    public void setExerciceId(Integer exerciceId) { this.exerciceId = exerciceId; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}
