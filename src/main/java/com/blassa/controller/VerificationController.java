package com.blassa.controller;

import com.blassa.dto.ResetPasswordRequest;
import com.blassa.dto.SendResetToEmail;
import com.blassa.service.AuthenticationService;
import com.blassa.service.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class VerificationController {

    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @GetMapping("/verify/email")
    public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {
        try {
            authenticationService.verifyEmail(token);
            response.sendRedirect(frontendUrl + "/email-verified?status=success");
        } catch (IllegalArgumentException e) {
            String errorMessage = java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            response.sendRedirect(frontendUrl + "/email-verified?status=error&message=" + errorMessage);
        }
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
