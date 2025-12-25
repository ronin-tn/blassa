package com.blassa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for refreshing mobile access tokens.
 */
@Data
public class MobileRefreshRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
