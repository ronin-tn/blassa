package com.blassa.controller;

import com.blassa.dto.AuthenticationRequest;
import com.blassa.dto.AuthenticationResponse;
import com.blassa.dto.RegisterRequest;
import com.blassa.service.AuthenticationService;
import com.blassa.service.GoogleOAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final GoogleOAuthService googleOAuthService;

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    @Value("${app.cookie.max-age:604800}") // jem3a kemla b seconds
    private int cookieMaxAge;

    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    private static final String COOKIE_NAME = "blassa_token";

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(
            @RequestBody @Valid AuthenticationRequest request,
            HttpServletResponse response) {
        AuthenticationResponse authResponse = authenticationService.authenticate(request);

        // mat5lich wehd mch mverifi email yodkhel
        String token = authResponse.getToken();
        if (token.equals("EMAIL_NOT_VERIFIED")) {
            // keno unverified maghir mt3ml cookie
            return ResponseEntity.ok(Map.of(
                    "status", "EMAIL_NOT_VERIFIED",
                    "email", authResponse.getEmail(),
                    "verificationSentAt", authResponse.getVerificationSentAt() != null
                            ? authResponse.getVerificationSentAt().toString()
                            : ""));
        }

        // asm3 lahne ani zedt httpOnly cookie m3a JWT token
        setTokenCookie(response, token);
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "email", authResponse.getEmail() != null ? authResponse.getEmail() : ""));
    }

    /**
     * Google OAuth code exchange endpoint.
     * Frontend gets auth code from Google directly, sends it here.
     * No sessions needed - fully stateless.
     */
    @PostMapping("/google")
    public ResponseEntity<Map<String, Object>> googleOAuthLogin(
            @RequestBody Map<String, String> request,
            HttpServletResponse response) {
        String code = request.get("code");
        String redirectUri = request.get("redirectUri");

        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "Authorization code is required"));
        }
        if (redirectUri == null || redirectUri.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "Redirect URI is required"));
        }

        try {
            String token = googleOAuthService.exchangeCodeForToken(code, redirectUri);
            setTokenCookie(response, token);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
            log.error("Google OAuth login failed", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", "Google authentication failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", cookieSameSite);
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("status", "LOGGED_OUT"));
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

    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        cookie.setAttribute("SameSite", cookieSameSite);
        response.addCookie(cookie);
    }
}
