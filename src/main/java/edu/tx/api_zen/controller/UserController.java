package edu.tx.api_zen.controller;

import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.dto.UserCreateDto;
import edu.tx.api_zen.dto.UserDto;
import edu.tx.api_zen.dto.UserUpdateDto;
import edu.tx.api_zen.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "Utilisateur", description = "Gestion des utilisateurs")
public class UserController {

    private final UserService userService;
    private final UserDao userDao;


    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister les utilisateurs")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.listAll());
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recuperer un utilisateur par id")
    public ResponseEntity<UserDto> get(@PathVariable int id) {
        Optional<UserDto> optionalUser = userService.getById(id);
        return optionalUser.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Creer un utilisateur")
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserCreateDto user) {
        UserDto created = userService.create(user);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    /**
     * Anonymisation RGPD déclenchée par l'admin (droit à l'oubli).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Anonymiser et désactiver un compte (RGPD)")
    public ResponseEntity<Void> anonymize(@PathVariable int id) {
        userService.anonymize(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * Suppression de son propre compte par l'utilisateur connecté.
     */
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Supprimer son propre compte (RGPD)")
    public ResponseEntity<Void> deleteSelf(@AuthenticationPrincipal UserDetails principal) {
        var user = userDao.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userService.anonymize(user.getId());
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre a jour un utilisateur")
    public ResponseEntity<UserDto> update(@PathVariable int id, @RequestBody @Valid UserUpdateDto userIncoming) {
        UserDto updated = userService.update(id, userIncoming);
        return ResponseEntity.ok(updated);
    }
}
