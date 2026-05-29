package edu.tx.api_zen.controller;

import edu.tx.api_zen.dto.ArticleCreateDto;
import edu.tx.api_zen.dto.ArticleDto;
import edu.tx.api_zen.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "Article", description = "Gestion des articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/list")
    @Operation(summary = "Lister tous les articles")
    public ResponseEntity<List<ArticleDto>> listAll() {
        return ResponseEntity.ok(articleService.listAll());
    }

    @GetMapping("/public")
    @Operation(summary = "Lister les articles publics")
    public ResponseEntity<List<ArticleDto>> listPublic() {
        return ResponseEntity.ok(articleService.listPublic());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un article par id")
    public ResponseEntity<ArticleDto> getById(@PathVariable Integer id) {
        Optional<ArticleDto> opt = articleService.getById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Creer un article")
    public ResponseEntity<ArticleDto> create(@RequestBody @Valid ArticleCreateDto dto) {
        ArticleDto saved = articleService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre a jour un article")
    public ResponseEntity<ArticleDto> update(@PathVariable Integer id, @RequestBody @Valid ArticleCreateDto dto) {
        ArticleDto updated = articleService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un article")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Televerser un fichier d'article")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "file is required"));
        }
        Path uploadsDir = Path.of("src/main/resources/static/uploads");
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }
        String filename = Path.of(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename()).getFileName().toString();
        Path target = uploadsDir.resolve(filename);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "failed to save file"));
        }
        String publicUrl = "/uploads/" + filename;
        Map<String, String> resp = new HashMap<>();
        resp.put("url", publicUrl);
        resp.put("filename", filename);
        return ResponseEntity.ok(resp);
    }
}
