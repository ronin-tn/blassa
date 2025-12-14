package com.blassa.security;

import com.blassa.model.entity.User;
import com.blassa.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Filter that blocks API access for users with incomplete profiles.
 * Users must have phone number, date of birth, and gender set.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileCompletionFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    // Paths that require complete profile (POST/PUT/DELETE actions)
    private static final List<String> PROTECTED_PATHS = List.of(
            "/api/v1/rides",
            "/api/v1/bookings",
            "/api/v1/reviews");

    // Paths that are always allowed (for profile completion and public access)
    private static final List<String> ALLOWED_PATHS = List.of(
            "/api/v1/user/me",
            "/api/v1/auth",
            "/api/v1/rides/search", // Public search
            "/api/v1/notifications" // Allow notifications
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip filter for non-protected paths or allowed paths
        if (!isProtectedPath(path) || isAllowedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get user email from authentication
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = userDetails.getUsername();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = userOpt.get();

        // Check if profile is complete
        if (!isProfileComplete(user)) {
            log.warn("Blocking API access for user {} - profile incomplete", email);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"PROFILE_INCOMPLETE\",\"message\":\"Please complete your profile before accessing this feature\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isProtectedPath(String path) {
        return PROTECTED_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isAllowedPath(String path) {
        return ALLOWED_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isProfileComplete(User user) {
        return user.getPhoneNumber() != null
                && !user.getPhoneNumber().isBlank()
                && user.getDateOfBirth() != null
                && user.getGender() != null;
    }
}
