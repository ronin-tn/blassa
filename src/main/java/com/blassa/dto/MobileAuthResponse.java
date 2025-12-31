package com.blassa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileAuthResponse {

    private String status;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

    private String email;
    private LocalDateTime verificationSentAt;
    private String message;
}
