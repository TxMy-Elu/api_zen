package edu.tx.api_zen.controller;

import edu.tx.api_zen.dto.LogActiviteDto;
import edu.tx.api_zen.service.LogActiviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log-activite")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Log Activite", description = "Consultation des journaux d'activite")
public class LogActiviteController {

    private final LogActiviteService logActiviteService;

    @GetMapping("/recent")
    @Operation(summary = "Lister les activites recentes")
    public ResponseEntity<List<LogActiviteDto>> listRecent() {
        return ResponseEntity.ok(logActiviteService.listRecent());
    }

    @GetMapping("/list")
    @Operation(summary = "Lister toutes les activites")
    public ResponseEntity<List<LogActiviteDto>> listAll() {
        return ResponseEntity.ok(logActiviteService.listAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lister les activites par utilisateur")
    public ResponseEntity<List<LogActiviteDto>> getByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(logActiviteService.getByUserId(userId));
    }

    @GetMapping("/table/{tableConcernee}")
    @Operation(summary = "Lister les activites par table")
    public ResponseEntity<List<LogActiviteDto>> getByTable(@PathVariable String tableConcernee) {
        return ResponseEntity.ok(logActiviteService.getByTable(tableConcernee));
    }

    @GetMapping("/action/{typeAction}")
    @Operation(summary = "Lister les activites par type d'action")
    public ResponseEntity<List<LogActiviteDto>> getByTypeAction(@PathVariable String typeAction) {
        return ResponseEntity.ok(logActiviteService.getByTypeAction(typeAction));
    }

    @GetMapping("/table/{tableConcernee}/{idEnregistrement}")
    @Operation(summary = "Lister les activites par table et identifiant")
    public ResponseEntity<List<LogActiviteDto>> getByTableAndId(
            @PathVariable String tableConcernee,
            @PathVariable Integer idEnregistrement) {
        return ResponseEntity.ok(logActiviteService.getByTableAndId(tableConcernee, idEnregistrement));
    }
}

