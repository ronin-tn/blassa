package com.blassa.controller;

import com.blassa.dto.*;
import com.blassa.model.entity.User;
import com.blassa.repository.UserRepository;
import com.blassa.security.JwtUtils;
import com.blassa.security.MyUserDetailsService;
import com.blassa.service.AuthenticationService;
import com.blassa.service.EmailService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.util.Collections.singletonList;

/**
 * web controller m3ch yasla7 l android 5ater yst3ml fi HTTP-ONLY COOKIES, aw 3mlt controller edha
 * ano nst3ml m3ah authenticationService w jawna fol, just lezmna nstockiw token directement
 * fi EncryptedSharedPreferences
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

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;


    @PostMapping("/register")
    public ResponseEntity<MobileAuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        AuthenticationResponse serviceResponse=authenticationService.register(request);
        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("REGISTRATION_SUCCESS")
                .email(serviceResponse.getEmail())
                .verificationSentAt(serviceResponse.getVerificationSentAt())
                .message("Please verify your email to continue")
                .build());
    }



    //Mobile login -asm3 lahne returni access token w refresh token fi response body.
    @PostMapping("/login")
    public ResponseEntity<MobileAuthResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isVerified()) {
            return ResponseEntity.ok(MobileAuthResponse.builder()
                    .status("EMAIL_NOT_VERIFIED")
                    .email(user.getEmail())
                    .verificationSentAt(user.getVerificationSentAt())
                    .message("Please verify your email before logging in")
                    .build());
        }
        UserDetails userDetails=userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken=jwtUtils.generateToken(userDetails);
        String refreshToken=jwtUtils.generateRefreshToken(userDetails);
        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("SUCCESS")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtils.getAccessTokenExpirationSeconds())
                .email(user.getEmail())
                .build());
    }

    //edhy bch tst79ha bch trefreshi access token bel refresh token tmchich todkhl b3dhk
    @PostMapping("/refresh")
    public ResponseEntity<MobileAuthResponse> refreshToken(@RequestBody @Valid MobileRefreshRequest request) {
        String refreshToken=request.getRefreshToken();
        if (!jwtUtils.isRefreshToken(refreshToken)) {
            return ResponseEntity.badRequest().body(MobileAuthResponse.builder()
                    .status("INVALID_TOKEN")
                    .message("Invalid refresh token")
                    .build());
        }

        String email=jwtUtils.extractUsername(refreshToken);
        UserDetails userDetails=userDetailsService.loadUserByUsername(email);

        if (!jwtUtils.isTokenValid(refreshToken, userDetails)) {
            return ResponseEntity.status(401).body(MobileAuthResponse.builder()
                    .status("TOKEN_EXPIRED")
                    .message("Refresh token has expired. Please login again.")
                    .build());
        }

        String newAccessToken=jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("SUCCESS")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtils.getAccessTokenExpirationSeconds())
                .build());
    }

   //edhy bch tresendi email verification
    @PostMapping("/resend-verification")
    public ResponseEntity<MobileAuthResponse> resendVerification(@RequestBody Map<String, String> request) {
        String email=request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(MobileAuthResponse.builder()
                    .status("ERROR")
                    .message("Email is required")
                    .build());
        }

        AuthenticationResponse serviceResponse=authenticationService.resendVerificationEmail(email);
        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("SUCCESS")
                .verificationSentAt(serviceResponse.getVerificationSentAt())
                .message("Verification email sent successfully")
                .build());
    }

    //Reset l pass
    @PostMapping("/forgot-password")
    public ResponseEntity<MobileAuthResponse> forgotPassword(@RequestBody Map<String, String> request) {
        String email=request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(MobileAuthResponse.builder()
                    .status("ERROR")
                    .message("Email is required")
                    .build());
        }
        try {
            emailService.sendForgotPasswordEmail(email);
        } catch (Exception e) {
            // fail w akhw
        }
        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("SUCCESS")
                .message("If this email exists, a password reset link has been sent")
                .build());
    }

   //logout l mobile
    @PostMapping("/logout")
    public ResponseEntity<MobileAuthResponse> logout() {
        //asm3 pour le moment bch nkhaliwha client-side (discardi tokens)
        //server-side khaleha mra okhra kn st7a9ina
        return ResponseEntity.ok(MobileAuthResponse.builder()
                .status("LOGGED_OUT")
                .message("Successfully logged out")
                .build());
    }


    //edhy l jme3a t3 gmail ani kamltha w testitha jawha fol
    @PostMapping("/google")
    public ResponseEntity<MobileAuthResponse> googleAuth(@RequestBody Map<String, String> request) {
        String idToken=request.get("idToken");
        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().body(MobileAuthResponse.builder()
                    .status("ERROR")
                    .message("ID token is required")
                    .build());
        }

        try {
            GoogleIdTokenVerifier verifier=new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken=verifier.verify(idToken);
            if (googleIdToken == null) {
                return ResponseEntity.status(401).body(MobileAuthResponse.builder()
                        .status("INVALID_TOKEN")
                        .message("Invalid Google ID token")
                        .build());
            }

           GoogleIdToken.Payload payload=googleIdToken.getPayload();
            String email=payload.getEmail();
            String googleId=payload.getSubject();
            String firstName=(String) payload.get("given_name");
            String lastName=(String) payload.get("family_name");
            String pictureUrl=(String) payload.get("picture");

            //chof ken nalgoh wla le b3d m3ml login si nn creatih
            User user=userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                //CREATIH LAHNE
                user=User.builder()
                        .email(email)
                        .firstName(firstName != null ? firstName : "User")
                        .lastName(lastName != null ? lastName : "")
                        .oauthProvider("google")
                        .oauthId(googleId)
                        .profilePictureUrl(pictureUrl)
                        .passwordHash(null)
                        .role(com.blassa.model.enums.UserRole.USER)
                        .isVerified(true)
                        .emailVerified(true)
                        .createdAt(java.time.OffsetDateTime.now())
                        .updatedAt(java.time.OffsetDateTime.now())
                        .build();
                userRepository.save(user);
            } else {
                if (user.getOauthId() == null) {
                    user.setOauthProvider("google");
                    user.setOauthId(googleId);
                    if (user.getProfilePictureUrl() == null && pictureUrl != null) {
                        user.setProfilePictureUrl(pictureUrl);
                    }
                    userRepository.save(user);
                }
            }
            //geneari refresh w access token
            UserDetails userDetails=userDetailsService.loadUserByUsername(email);
            String accessToken=jwtUtils.generateToken(userDetails);
            String refreshToken=jwtUtils.generateRefreshToken(userDetails);

            return ResponseEntity.ok(MobileAuthResponse.builder()
                    .status("SUCCESS")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtUtils.getAccessTokenExpirationSeconds())
                    .email(email)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(500).body(MobileAuthResponse.builder()
                    .status("ERROR")
                    .message("Failed to verify Google token: " + e.getMessage())
                    .build());
        }
    }
}
