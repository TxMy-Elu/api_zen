package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public class ParticipationRequest {
    @Schema(example = "2")
    private Integer userId;
    @Schema(example = "2026-03-30T10:15:30Z")
    private Instant completedAt;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}

