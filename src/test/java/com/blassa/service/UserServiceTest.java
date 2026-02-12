package com.blassa.service;

import com.blassa.dto.ChangePasswordRequest;
import com.blassa.model.entity.User;
import com.blassa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.blassa.repository.RideRepository rideRepository;

    @Mock
    private com.blassa.repository.ReviewRepository reviewRepository;

    @Mock
    private com.blassa.repository.BookingRepository bookingRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private EmailService emailService;

    @Mock
    private com.blassa.security.JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("rayen@blassa.tn")
                .firstName("Rayen")
                .lastName("Test")
                .passwordHash("hashedPassword123")
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("rayen@blassa.tn")
                .password("hashedPassword123")
                .authorities("USER")
                .build();

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        lenient().when(userRepository.findByEmail("rayen@blassa.tn")).thenReturn(Optional.of(testUser));
    }

    @Test
    void changePassword_shouldSucceed_whenCurrentPasswordMatches() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass123", "newPass123");

        when(passwordEncoder.matches("oldPass", "hashedPassword123")).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("newHashedPassword");

        userService.changePassword(request);

        assertEquals("newHashedPassword", testUser.getPasswordHash());
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_shouldThrow_whenCurrentPasswordIsWrong() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPass", "newPass123", "newPass123");

        when(passwordEncoder.matches("wrongPass", "hashedPassword123")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(request));

        assertEquals("Le mot de passe actuel est incorrect", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldThrow_whenNewPasswordsDoNotMatch() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass123", "differentPass");

        when(passwordEncoder.matches("oldPass", "hashedPassword123")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(request));

        assertEquals("Les mots de passe ne correspondent pas", ex.getMessage());
        verify(userRepository, never()).save(any());
    }
}
