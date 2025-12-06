package com.blassa.service;

import com.blassa.dto.*;
import com.blassa.model.entity.User;
import com.blassa.repository.UserRepository;
import com.blassa.security.JwtUtils;
import com.blassa.security.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), jwt);
        return new AuthenticationResponse("Registration successful. Please verify your email.");
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
            var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            var newToken = jwtUtils.generateToken(userDetails);
            user.setVerificationToken(newToken);
            userRepository.save(user);
            emailService.sendVerificationEmail(user.getEmail(), newToken);
            return new AuthenticationResponse("We've sent you a verification email. Please check your inbox.");
        }

        var userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        var jwt = jwtUtils.generateToken(userDetails);
        return new AuthenticationResponse(jwt);
    }

    @Transactional
    public void resetPassword(String token, ResetPasswordRequest request) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));
        if (!jwtUtils.isTokenValid(token, userDetailsService.loadUserByUsername(user.getEmail()))) {
            throw new IllegalArgumentException("Verification token has expired");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setVerificationToken(null);
        userRepository.save(user);
    }

}
