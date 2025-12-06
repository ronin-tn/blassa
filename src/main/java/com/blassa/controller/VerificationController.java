package com.blassa.controller;

import com.blassa.dto.ResetPasswordRequest;
import com.blassa.dto.SendResetToEmail;
import com.blassa.service.AuthenticationService;
import com.blassa.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VerificationController {

    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @GetMapping("/verify/email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authenticationService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully! You can now login.");
    }

    @PostMapping("/reset/email")
    public ResponseEntity<String> resetEmail(@RequestParam String token,
            @RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(token, request);
        return ResponseEntity.ok("Password reset successfully! You can now login.");
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgot(@RequestBody @Valid SendResetToEmail request) {
        emailService.sendForgotPasswordEmail(request.getEmail());
        return ResponseEntity.ok("A password reset email has been sent to you.");
    }
}
