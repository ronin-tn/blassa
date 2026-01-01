package com.blassa.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AuthenticationResponse {

    private String token;
    private String email;
    private LocalDateTime verificationSentAt;

    public AuthenticationResponse(String token) {
        this.token = token;
    }
}
