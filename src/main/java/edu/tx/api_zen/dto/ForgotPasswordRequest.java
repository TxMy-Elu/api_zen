package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {

    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    @Schema(example = "utilisateur@example.com")
    private String email;
}

