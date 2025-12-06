package com.blassa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendResetToEmail {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
}
