package edu.tx.api_zen.controller;

import edu.tx.api_zen.dto.LogConnexionDto;
import edu.tx.api_zen.service.LogConnexionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log-connexion")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Log Connexion", description = "Consultation des journaux de connexion")
public class LogConnexionController {

    private final LogConnexionService logConnexionService;

    @GetMapping("/recent")
    @Operation(summary = "Lister les connexions recentes")
    public ResponseEntity<List<LogConnexionDto>> listRecent() {
        return ResponseEntity.ok(logConnexionService.listRecent());
    }

    @GetMapping("/list")
    @Operation(summary = "Lister toutes les connexions")
    public ResponseEntity<List<LogConnexionDto>> listAll() {
        return ResponseEntity.ok(logConnexionService.listAll());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Lister les connexions par email")
    public ResponseEntity<List<LogConnexionDto>> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(logConnexionService.getByEmail(email));
    }

    @GetMapping("/success")
    @Operation(summary = "Lister les connexions reussies")
    public ResponseEntity<List<LogConnexionDto>> getSuccess() {
        return ResponseEntity.ok(logConnexionService.getByReussite(true));
    }

    @GetMapping("/failed")
    @Operation(summary = "Lister les connexions echouees")
    public ResponseEntity<List<LogConnexionDto>> getFailed() {
        return ResponseEntity.ok(logConnexionService.getByReussite(false));
    }

    @GetMapping("/ip/{ip}")
    @Operation(summary = "Lister les connexions par adresse IP")
    public ResponseEntity<List<LogConnexionDto>> getByIp(@PathVariable String ip) {
        return ResponseEntity.ok(logConnexionService.getByIp(ip));
    }
}

