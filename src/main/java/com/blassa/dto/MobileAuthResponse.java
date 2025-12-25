package com.blassa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Mobile-specific authentication response.
 * Returns tokens in response body (unlike web which uses HTTP-only cookies).
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileAuthResponse {

    private String status;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn; // seconds until access token expires

    // For registration/verification flows
    private String email;
    private LocalDateTime verificationSentAt;
    private String message;
}
