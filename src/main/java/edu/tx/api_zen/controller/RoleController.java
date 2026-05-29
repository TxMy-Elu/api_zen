package edu.tx.api_zen.controller;

import edu.tx.api_zen.dao.RoleDao;
import edu.tx.api_zen.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
@Tag(name = "Role", description = "Consultation des roles")
public class RoleController {

    private final RoleDao roleDao;

    @GetMapping("/list")
    @Operation(summary = "Lister les roles")
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(roleDao.findAll());
    }

}
