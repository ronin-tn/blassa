package com.blassa.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles successful OAuth2 authentication by generating a JWT token
 * and setting it as an httpOnly cookie before redirecting to the frontend.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    @Value("${app.cookie.max-age:604800}") // 7 days in seconds
    private int cookieMaxAge;

    private static final String COOKIE_NAME = "blassa_token";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        log.info("OAuth2 authentication successful for: {}", email);

        // Load user details and generate JWT
        var userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtUtils.generateToken(userDetails);

        // Set httpOnly cookie
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);

        // Redirect to frontend without token in URL
        String redirectUrl = frontendUrl + "/oauth-callback";
        log.info("Redirecting OAuth2 user to: {}", redirectUrl);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
