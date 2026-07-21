package edu.tx.api_zen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Le token est requis")
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private String token;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Pattern(regexp = "[^0-9]*[0-9].*", message = "Le mot de passe doit contenir au moins un chiffre")
    @Pattern(regexp = "[^!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*", message = "Le mot de passe doit contenir au moins un caractère spécial")
    @Schema(example = "NouveauMotDePasse123!")
    private String newPassword;
}

