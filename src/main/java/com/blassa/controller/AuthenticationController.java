package com.blassa.controller;

import com.blassa.dto.AuthenticationRequest;
import com.blassa.dto.AuthenticationResponse;
import com.blassa.dto.RegisterRequest;
import com.blassa.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<AuthenticationResponse> resendVerification(
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        return ResponseEntity.ok(authenticationService.resendVerificationEmail(email));
    }

    @PostMapping("/change-email")
    public ResponseEntity<AuthenticationResponse> changeEmail(
            @RequestBody Map<String, String> request) {
        String currentEmail = request.get("currentEmail");
        String newEmail = request.get("newEmail");
        String password = request.get("password");

        if (currentEmail == null || currentEmail.isBlank()) {
            throw new IllegalArgumentException("Current email is required");
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("New email is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        return ResponseEntity.ok(
                authenticationService.changeEmailForUnverifiedUser(currentEmail, newEmail, password));
    }
}
