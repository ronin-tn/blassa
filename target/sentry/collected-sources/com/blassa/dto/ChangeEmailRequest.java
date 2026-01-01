package com.blassa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailRequest {

    @NotBlank(message = "Le nouvel email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String newEmail;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}
