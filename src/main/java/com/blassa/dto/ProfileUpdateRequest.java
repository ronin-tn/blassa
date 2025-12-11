package com.blassa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String lastName;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numéro de téléphone invalide")
    private String phoneNumber;

    @Size(max = 500, message = "La bio ne doit pas dépasser 500 caractères")
    private String bio;

    @Size(max = 255, message = "L'URL Facebook ne doit pas dépasser 255 caractères")
    private String facebookUrl;

    @Size(max = 255, message = "L'URL Instagram ne doit pas dépasser 255 caractères")
    private String instagramUrl;
}
