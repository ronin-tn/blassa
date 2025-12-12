package com.blassa.service;

import com.blassa.dto.*;
import com.blassa.model.entity.User;
import com.blassa.repository.UserRepository;
import com.blassa.security.JwtUtils;
import com.blassa.security.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private final EmailService emailService;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .dateOfBirth(request.getBirthDate())
                .role(request.getRole())
                .isVerified(false)
                .build();
        userRepository.save(user);

        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        var jwt = jwtUtils.generateToken(userDetails);
        user.setVerificationToken(jwt);
        user.setVerificationSentAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), jwt);

        AuthenticationResponse response = new AuthenticationResponse("REGISTRATION_SUCCESS");
        response.setEmail(user.getEmail());
        response.setVerificationSentAt(LocalDateTime.now());
        return response;
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));
        if (!jwtUtils.isTokenValid(token, userDetailsService.loadUserByUsername(user.getEmail()))) {
            throw new IllegalArgumentException("Verification token has expired");
        }
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationSentAt(null);
        userRepository.save(user);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        // Validate credentials FIRST (security: don't leak email existence)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()));

        User user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check verification status after password validation
        if (!user.isVerified()) {
            // Return special response indicating email verification needed
            AuthenticationResponse response = new AuthenticationResponse("EMAIL_NOT_VERIFIED");
            response.setEmail(user.getEmail());
            response.setVerificationSentAt(user.getVerificationSentAt());
            return response;
        }

        var userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        var jwt = jwtUtils.generateToken(userDetails);
        return new AuthenticationResponse(jwt);
    }

    @Transactional
    public AuthenticationResponse resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }

        // Check if we sent an email recently (rate limiting-60 seconds)
        if (user.getVerificationSentAt() != null &&
                user.getVerificationSentAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
            long secondsRemaining = java.time.Duration.between(
                    LocalDateTime.now(),
                    user.getVerificationSentAt().plusSeconds(60)).getSeconds();
            throw new IllegalArgumentException(
                    "Please wait " + secondsRemaining + " seconds before requesting a new email");
        }

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var newToken = jwtUtils.generateToken(userDetails);
        user.setVerificationToken(newToken);
        user.setVerificationSentAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), newToken);

        AuthenticationResponse response = new AuthenticationResponse("Verification email sent successfully");
        response.setVerificationSentAt(LocalDateTime.now());
        return response;
    }

    @Transactional
    public void resetPassword(String token, ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        // Update password and clear reset token
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setResetToken(null);
        userRepository.save(user);
    }

    @Transactional
    public AuthenticationResponse changeEmailForUnverifiedUser(String currentEmail, String newEmail, String password) {
        // Validate credentials first
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(currentEmail, password));
        } catch (Exception e) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isVerified()) {
            throw new IllegalArgumentException("Cannot change email for verified accounts");
        }

        // Check if new email is already taken
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée");
        }

        // Update email and send new verification
        user.setEmail(newEmail);
        var newToken = jwtUtils.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(newEmail)
                        .password(user.getPasswordHash())
                        .authorities("USER")
                        .build());
        user.setVerificationToken(newToken);
        user.setVerificationSentAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendVerificationEmail(newEmail, newToken);

        AuthenticationResponse response = new AuthenticationResponse("Email updated successfully");
        response.setEmail(newEmail);
        response.setVerificationSentAt(LocalDateTime.now());
        return response;
    }

}
