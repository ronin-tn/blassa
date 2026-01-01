package com.blassa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MobileRefreshRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
