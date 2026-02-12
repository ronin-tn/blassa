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

    @Value("${app.cookie.max-age:604800}") // jem3a kemla
    private int cookieMaxAge;

    @Value("${app.cookie.same-site:None}")
    private String cookieSameSite;

    private static final String COOKIE_NAME = "blassa_token";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        try {
            var userDetails = userDetailsService.loadUserByUsername(email);
            String token = jwtUtils.generateToken(userDetails);

            // savi token fi httpOnly cookie
            Cookie cookie = new Cookie(COOKIE_NAME, token);
            cookie.setHttpOnly(true);
            cookie.setSecure(secureCookie);
            cookie.setPath("/");
            cookie.setMaxAge(cookieMaxAge);
            cookie.setAttribute("SameSite", cookieSameSite);
            response.addCookie(cookie);
            String redirectUrl = frontendUrl + "/oauth-callback";

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (org.springframework.security.authentication.LockedException e) {
            String redirectUrl = frontendUrl + "/login?error=banned";
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
