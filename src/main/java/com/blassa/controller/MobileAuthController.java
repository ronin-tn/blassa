package com.blassa.controller;

import com.blassa.dto.*;
import com.blassa.model.entity.User;
import com.blassa.repository.UserRepository;
import com.blassa.security.JwtUtils;
import com.blassa.security.MyUserDetailsService;
import com.blassa.service.AuthenticationService;
import com.blassa.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Mobile-specific authentication endpoints.
 * 
 * Unlike the web controller (which uses HTTP-only cookies), this controller
 * returns tokens directly in the response body for mobile clients to store
 * in EncryptedSharedPreferences / Android Keystore.
 * 
 * Shares all business logic with AuthenticationService - only token delivery
 * differs.
 */
@RestController
@RequestMapping("/api/v1/auth/mobile")
@RequiredArgsConstructor
public class MobileAuthController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Mobile registration - same as web, returns tokens in response body.
     */
    @PostMapping("/register")
    public ResponseEntity<MobileAuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        // Delegate to shared service for business logic
        AuthenticationResponse serviceResponse = authenticationService.register(request);

        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("REGISTRATION_SUCCESS")
                .email(serviceResponse.getEmail())
                .verificationSentAt(serviceResponse.getVerificationSentAt())
                .message("Please verify your email to continue")
                .build());
    }

    /**
     * Mobile login - returns access token and refresh token in response body.
     */
    @PostMapping("/login")
    public ResponseEntity<MobileAuthResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        // Validate credentials using shared auth manager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check verification status
        if (!user.isVerified()) {
            return ResponseEntity.ok(MobileAuthResponse.builder()
                    .status("EMAIL_NOT_VERIFIED")
                    .email(user.getEmail())
                    .verificationSentAt(user.getVerificationSentAt())
                    .message("Please verify your email before logging in")
                    .build());
        }

        // Generate tokens for mobile
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("SUCCESS")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtils.getAccessTokenExpirationSeconds())
                .email(user.getEmail())
                .build());
    }

    /**
     * Refresh access token using refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<MobileAuthResponse> refreshToken(@RequestBody @Valid MobileRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtUtils.isRefreshToken(refreshToken)) {
            return ResponseEntity.badRequest().body(MobileAuthResponse.builder()
                    .status("INVALID_TOKEN")
                    .message("Invalid refresh token")
                    .build());
        }

        String email = jwtUtils.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtUtils.isTokenValid(refreshToken, userDetails)) {
            return ResponseEntity.status(401).body(MobileAuthResponse.builder()
                    .status("TOKEN_EXPIRED")
                    .message("Refresh token has expired. Please login again.")
                    .build());
        }

        // Generate new access token (keep same refresh token)
        String newAccessToken = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("SUCCESS")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Return same refresh token
                .expiresIn(jwtUtils.getAccessTokenExpirationSeconds())
                .build());
    }

    /**
     * Resend verification email.
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<MobileAuthResponse> resendVerification(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(MobileAuthResponse.builder()
                    .status("ERROR")
                    .message("Email is required")
                    .build());
        }

        AuthenticationResponse serviceResponse = authenticationService.resendVerificationEmail(email);

        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("SUCCESS")
                .verificationSentAt(serviceResponse.getVerificationSentAt())
                .message("Verification email sent successfully")
                .build());
    }

    /**
     * Request password reset (send email).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MobileAuthResponse> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(MobileAuthResponse.builder()
                    .status("ERROR")
                    .message("Email is required")
                    .build());
        }

        // Use existing email service method - it handles token generation internally
        try {
            emailService.sendForgotPasswordEmail(email);
        } catch (Exception e) {
            // Silently fail to prevent email enumeration
        }

        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("SUCCESS")
                .message("If this email exists, a password reset link has been sent")
                .build());
    }

    /**
     * Mobile logout - client should discard tokens locally.
     * This endpoint can be used for server-side token revocation if needed.
     */
    @PostMapping("/logout")
    public ResponseEntity<MobileAuthResponse> logout() {
        // For mobile, logout is primarily client-side (discard tokens)
        // Server-side revocation can be added here if needed
        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("LOGGED_OUT")
                .message("Successfully logged out")
                .build());
    }
}
