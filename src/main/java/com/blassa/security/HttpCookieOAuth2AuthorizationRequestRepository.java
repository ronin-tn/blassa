package com.blassa.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;
import java.util.Set;

/**
 * Cookie-based OAuth2 authorization request repository.
 * Uses a simple DTO to serialize/deserialize since OAuth2AuthorizationRequest
 * lacks a default constructor and cannot be directly handled by Jackson.
 */
@Component
@Slf4j
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_EXPIRE_SECONDS = 300;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Simple DTO that mirrors OAuth2AuthorizationRequest fields.
     * Jackson can serialize/deserialize this since it has @JsonCreator.
     */
    record AuthRequestDto(
            @JsonProperty("au") String authorizationUri,
            @JsonProperty("ci") String clientId,
            @JsonProperty("ru") String redirectUri,
            @JsonProperty("sc") Set<String> scopes,
            @JsonProperty("st") String state,
            @JsonProperty("ap") Map<String, Object> additionalParameters,
            @JsonProperty("aru") String authorizationRequestUri) {
        @JsonCreator
        AuthRequestDto {
        }

        static AuthRequestDto from(OAuth2AuthorizationRequest request) {
            return new AuthRequestDto(
                    request.getAuthorizationUri(),
                    request.getClientId(),
                    request.getRedirectUri(),
                    request.getScopes(),
                    request.getState(),
                    request.getAdditionalParameters(),
                    request.getAuthorizationRequestUri());
        }

        OAuth2AuthorizationRequest toOAuth2Request() {
            return OAuth2AuthorizationRequest.authorizationCode()
                    .authorizationUri(authorizationUri)
                    .clientId(clientId)
                    .redirectUri(redirectUri)
                    .scopes(scopes)
                    .state(state)
                    .additionalParameters(additionalParameters)
                    .authorizationRequestUri(authorizationRequestUri)
                    .build();
        }
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return getCookieValue(request);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeCookie(response);
            return;
        }

        try {
            AuthRequestDto dto = AuthRequestDto.from(authorizationRequest);
            String json = objectMapper.writeValueAsString(dto);
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes());

            Cookie cookie = new Cookie(COOKIE_NAME, encoded);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
            response.addCookie(cookie);
            log.debug("Saved OAuth2 authorization request to cookie, state={}", authorizationRequest.getState());
        } catch (Exception e) {
            log.error("Failed to serialize OAuth2 authorization request", e);
            throw new RuntimeException("Failed to serialize OAuth2 authorization request", e);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authRequest = loadAuthorizationRequest(request);
        if (authRequest != null) {
            removeCookie(response);
        }
        return authRequest;
    }

    private OAuth2AuthorizationRequest getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;

        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                try {
                    String json = new String(Base64.getUrlDecoder().decode(cookie.getValue()));
                    AuthRequestDto dto = objectMapper.readValue(json, AuthRequestDto.class);
                    return dto.toOAuth2Request();
                } catch (Exception e) {
                    log.error("Failed to deserialize OAuth2 authorization request from cookie", e);
                    return null;
                }
            }
        }
        return null;
    }

    private void removeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
