package edu.tx.api_zen.controller;

import edu.tx.api_zen.dto.ExerciceCreateDto;
import edu.tx.api_zen.dto.ExerciceDto;
import edu.tx.api_zen.service.ExerciceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercice")
@Tag(name = "Exercice", description = "Gestion des exercices")
public class ExerciceController {

    private final ExerciceService exerciceService;

    @GetMapping("/list")
    @Operation(summary = "Lister tous les exercices")
    public ResponseEntity<List<ExerciceDto>> list() {
        return ResponseEntity.ok(exerciceService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un exercice par id")
    public ResponseEntity<ExerciceDto> getById(@PathVariable Integer id) {
        Optional<ExerciceDto> opt = exerciceService.getById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Creer un exercice")
    public ResponseEntity<ExerciceDto> create(@RequestBody ExerciceCreateDto dto) {
        ExerciceDto saved = exerciceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre a jour un exercice")
    public ResponseEntity<ExerciceDto> update(@PathVariable Integer id, @RequestBody ExerciceCreateDto dto) {
        ExerciceDto updated = exerciceService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un exercice")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        exerciceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
