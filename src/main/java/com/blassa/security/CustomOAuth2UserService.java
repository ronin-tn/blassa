package com.blassa.security;

import com.blassa.model.entity.User;
import com.blassa.model.enums.UserRole;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Custom OAuth2 user service that handles user creation/retrieval for OAuth2
 * login.
 * When a user logs in with Google, this service:
 * 1. Loads user info from Google
 * 2. Creates a new user if they don't exist (auto-registration)
 * 3. Links Google account to existing user if email matches
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        String googleId = (String) attributes.get("sub");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");
        String pictureUrl = (String) attributes.get("picture");

        log.info("OAuth2 login attempt for email: {} with provider: {}", email, registrationId);

        // Check if user exists by OAuth ID first
        Optional<User> existingUserByOAuth = userRepository.findByOauthProviderAndOauthId(registrationId, googleId);

        if (existingUserByOAuth.isPresent()) {
            log.info("Found existing user by OAuth ID: {}", email);
            return oauth2User;
        }

        // Check if user exists by email (might have registered with email/password
        // first)
        Optional<User> existingUserByEmail = userRepository.findByEmail(email);

        if (existingUserByEmail.isPresent()) {
            // Link OAuth to existing account
            User user = existingUserByEmail.get();
            user.setOauthProvider(registrationId);
            user.setOauthId(googleId);
            if (user.getProfilePictureUrl() == null && pictureUrl != null) {
                user.setProfilePictureUrl(pictureUrl);
            }
            userRepository.save(user);
            log.info("Linked OAuth to existing user: {}", email);
            return oauth2User;
        }

        // Create new user
        User newUser = User.builder()
                .email(email)
                .firstName(firstName != null ? firstName : "User")
                .lastName(lastName != null ? lastName : "")
                .oauthProvider(registrationId)
                .oauthId(googleId)
                .profilePictureUrl(pictureUrl)
                .passwordHash(null) // OAuth users don't have passwords
                .phoneNumber(null) // Will need to be updated by user in profile
                .dateOfBirth(null) // Will need to be updated by user in profile
                .gender(null) // Will need to be updated by user in profile
                .role(UserRole.USER)
                .isVerified(true) // Google accounts are verified by Google
                .emailVerified(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        userRepository.save(newUser);
        log.info("Created new OAuth user: {}", email);

        return oauth2User;
    }
}
