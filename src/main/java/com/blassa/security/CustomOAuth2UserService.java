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

/*
 Service hedha lel OAuth2 login, creation wala retrieval mta3 users.
Ki user ylogin b Google:
Yjib user info men Google
Ken user mch mawjoud, y3amel creation automatique (auto-registration)
Ken email mawjouda, yrabet compte Google b user mawjoud
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
        Optional<User> existingUserByOAuth = userRepository.findByOauthProviderAndOauthId(registrationId, googleId);

        if (existingUserByOAuth.isPresent()) {
            return oauth2User;
        }

        Optional<User> existingUserByEmail = userRepository.findByEmail(email);

        if (existingUserByEmail.isPresent()) {
            User user = existingUserByEmail.get();
            user.setOauthProvider(registrationId);
            user.setOauthId(googleId);
            if (user.getProfilePictureUrl() == null && pictureUrl != null) {
                user.setProfilePictureUrl(pictureUrl);
            }
            userRepository.save(user);
            return oauth2User;
        }

        User newUser = User.builder()
                .email(email)
                .firstName(firstName != null ? firstName : "User")
                .lastName(lastName != null ? lastName : "")
                .oauthProvider(registrationId)
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

        return oauth2User;
    }
}
