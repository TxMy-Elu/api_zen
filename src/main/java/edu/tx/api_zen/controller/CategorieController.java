package edu.tx.api_zen.controller;

import edu.tx.api_zen.dto.CategorieCreateDto;
import edu.tx.api_zen.dto.CategorieDto;
import edu.tx.api_zen.service.CategorieService;
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
@RequestMapping("/api/categorie")
@Tag(name = "Categorie", description = "Gestion des categories")
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping("/list")
    @Operation(summary = "Lister toutes les categories")
    public ResponseEntity<List<CategorieDto>> list() {
        return ResponseEntity.ok(categorieService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer une categorie par id")
    public ResponseEntity<CategorieDto> getById(@PathVariable Integer id) {
        Optional<CategorieDto> opt = categorieService.getById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Creer une categorie")
    public ResponseEntity<CategorieDto> create(@RequestBody CategorieCreateDto dto) {
        CategorieDto saved = categorieService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre a jour une categorie")
    public ResponseEntity<CategorieDto> update(@PathVariable Integer id, @RequestBody CategorieCreateDto dto) {
        CategorieDto updated = categorieService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une categorie")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        categorieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

