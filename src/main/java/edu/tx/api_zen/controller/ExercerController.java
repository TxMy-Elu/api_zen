package edu.tx.api_zen.controller;

import edu.tx.api_zen.dto.ExercerCreateDto;
import edu.tx.api_zen.dto.ExercerDto;
import edu.tx.api_zen.service.ExercerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercer")
@Tag(name = "Exercer", description = "Suivi des participations aux exercices")
public class ExercerController {

    private final ExercerService exercerService;

    @GetMapping("/list")
    @Operation(summary = "Lister toutes les participations")
    public ResponseEntity<List<ExercerDto>> listAll() {
        return ResponseEntity.ok(exercerService.listAllPublic());
    }

    @GetMapping("/public")
    @Operation(summary = "Lister les participations publiques")
    public ResponseEntity<List<ExercerDto>> listPublic() {
        return ResponseEntity.ok(exercerService.listAllPublic());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les participations d'un utilisateur")
    public ResponseEntity<List<ExercerDto>> listByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(exercerService.listByUser(userId));
    }

    @GetMapping("/exercice/{exerciceId}")
    @Operation(summary = "Lister les participations d'un exercice")
    public ResponseEntity<List<ExercerDto>> listByExercice(@PathVariable Integer exerciceId) {
        return ResponseEntity.ok(exercerService.listByExercice(exerciceId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer une participation par id")
    public ResponseEntity<ExercerDto> getById(@PathVariable Integer id) {
        return exercerService.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une participation")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        exercerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Creer une participation")
    public ResponseEntity<ExercerDto> create(@RequestBody @Valid ExercerCreateDto dto) {
        Instant completedAt = null;
        if (dto.getCompletedAt() != null && !dto.getCompletedAt().isBlank()) {
            try {
                completedAt = Instant.parse(dto.getCompletedAt());
            } catch (DateTimeException ex) {
                throw new IllegalArgumentException("completedAt must be a valid ISO-8601 timestamp, e.g. 2026-03-04T10:15:30Z");
            }
        }
        ExercerDto saved = exercerService.create(dto.getUserId(), dto.getExerciceId(), completedAt);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
