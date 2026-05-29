package edu.tx.api_zen.controller;

import edu.tx.api_zen.dto.ConsulterCreateDto;
import edu.tx.api_zen.dto.ConsulterDto;
import edu.tx.api_zen.service.ConsulterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consulter")
@Tag(name = "Consulter", description = "Suivi des consultations d'articles")
public class ConsulterController {

    private final ConsulterService consulterService;

    @GetMapping("/list")
    @Operation(summary = "Lister les consultations")
    public ResponseEntity<List<ConsulterDto>> list() {
        return ResponseEntity.ok(consulterService.listAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les consultations d'un utilisateur")
    public ResponseEntity<List<ConsulterDto>> getByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(consulterService.getByUserId(userId));
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Lister les consultations d'un article")
    public ResponseEntity<List<ConsulterDto>> getByArticle(@PathVariable Integer articleId) {
        return ResponseEntity.ok(consulterService.getByArticleId(articleId));
    }

    @GetMapping("/article/{articleId}/count")
    @Operation(summary = "Compter les vues d'un article")
    public ResponseEntity<Map<String, Long>> countByArticle(@PathVariable Integer articleId) {
        long count = consulterService.countViewsByArticle(articleId);
        return ResponseEntity.ok(Map.of("views", count));
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Compter les articles consultes par un utilisateur")
    public ResponseEntity<Map<String, Long>> countByUser(@PathVariable Integer userId) {
        long count = consulterService.countArticlesByUser(userId);
        return ResponseEntity.ok(Map.of("articlesConsultes", count));
    }

    @PostMapping
    @Operation(summary = "Creer une consultation")
    public ResponseEntity<ConsulterDto> create(@Valid @RequestBody ConsulterCreateDto dto) {
        ConsulterDto saved = consulterService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une consultation")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        consulterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

