package com.blassa.service;

import com.blassa.model.entity.User;
import com.blassa.model.enums.UserRole;
import com.blassa.repository.UserRepository;
import com.blassa.security.JwtUtils;
import com.blassa.security.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Handles Google OAuth2 code exchange flow for stateless deployments.
 * The frontend redirects to Google directly, gets an auth code,
 * and sends it here to exchange for user info + JWT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    /**
     * Exchange Google authorization code for user info, create/find user, return
     * JWT.
     */
    @Transactional
    public String exchangeCodeForToken(String code, String redirectUri) {
        // 1. Exchange code for access token
        String accessToken = exchangeCodeForAccessToken(code, redirectUri);

        // 2. Fetch user info from Google
        Map<String, Object> userInfo = fetchGoogleUserInfo(accessToken);

        // 3. Create or find user
        String email = (String) userInfo.get("email");
        String googleId = (String) userInfo.get("sub");
        String firstName = (String) userInfo.get("given_name");
        String lastName = (String) userInfo.get("family_name");
        String pictureUrl = (String) userInfo.get("picture");

        processGoogleUser(email, googleId, firstName, lastName, pictureUrl);

        // 4. Generate JWT
        var userDetails = userDetailsService.loadUserByUsername(email);
        return jwtUtils.generateToken(userDetails);
    }

    private String exchangeCodeForAccessToken(String code, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(TOKEN_URL, request, Map.class);

        if (response == null || !response.containsKey("access_token")) {
            log.error("Failed to exchange code for token. Response: {}", response);
            throw new RuntimeException("Failed to exchange authorization code");
        }

        return (String) response.get("access_token");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                USERINFO_URL, HttpMethod.GET, request, (Class<Map<String, Object>>) (Class<?>) Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to fetch Google user info");
        }

        return response.getBody();
    }

    private void processGoogleUser(String email, String googleId,
            String firstName, String lastName, String pictureUrl) {
        Optional<User> existingByOAuth = userRepository.findByOauthProviderAndOauthId("google", googleId);
        if (existingByOAuth.isPresent()) {
            return; // User already linked
        }

        Optional<User> existingByEmail = userRepository.findByEmail(email);
        if (existingByEmail.isPresent()) {
            User user = existingByEmail.get();
            user.setOauthProvider("google");
            user.setOauthId(googleId);
            if (user.getProfilePictureUrl() == null && pictureUrl != null) {
                user.setProfilePictureUrl(pictureUrl);
            }
            userRepository.save(user);
            return;
        }

        // New user
        User newUser = User.builder()
                .email(email)
                .firstName(firstName != null ? firstName : "User")
                .lastName(lastName != null ? lastName : "")
                .oauthProvider("google")
                .oauthId(googleId)
                .profilePictureUrl(pictureUrl)
                .passwordHash(null)
                .phoneNumber(null)
                .dateOfBirth(null)
                .gender(null)
                .role(UserRole.USER)
                .isVerified(true)
                .emailVerified(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        userRepository.save(newUser);
    }
}
