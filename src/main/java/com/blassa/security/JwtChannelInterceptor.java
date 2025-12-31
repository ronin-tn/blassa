package com.blassa.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Interceptor hedha yauthentifi WebSocket connections b JWT.
 * Ki client yconnecti b STOMP, yb3ath token fi header “Authorization” .
 * Interceptor yakhou token, yverifyh, w ken sahih y3ayen user principal lel
 * connection.
 * Ba3d, kol message 3la nafs connection yet3add authenticated b nafs user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);

                try {
                    // Extract username (email) from JWT
                    String userEmail = jwtUtils.extractUsername(jwt);

                    if (userEmail != null) {
                        // Load user details from database
                        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                        // Validati token
                        if (jwtUtils.isTokenValid(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                            accessor.setUser(authToken);
                        }
                    }
                } catch (Exception e) {
                }
            } else {

                log.warn("WebSocket CONNECT without Authorization header");
            }
        }

        return message;
    }
}
