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
 * JWT Channel Interceptor for WebSocket (STOMP) Authentication
 * 
 * This interceptor authenticates WebSocket connections using JWT tokens.
 * Unlike HTTP requests which use JwtAuthenticationFilter, WebSocket connections
 * use STOMP protocol which sends headers differently.
 * 
 * HOW IT WORKS:
 * 1. When a client connects via WebSocket, they send a STOMP CONNECT frame
 * 2. The JWT token is passed in the "Authorization" header of this CONNECT
 * frame
 * 3. This interceptor extracts and validates the token
 * 4. If valid, it sets the user principal on the connection
 * 5. All subsequent messages on this connection are authenticated as that user
 * 
 * WHY WE NEED THIS:
 * - Spring Security's HTTP filters don't apply to WebSocket messages
 * - WebSocket uses a persistent connection, so we authenticate once at CONNECT
 * - The user principal is then used by
 * SimpMessagingTemplate.convertAndSendToUser()
 * to route messages to the correct user
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;

    /**
     * Called before a message is sent to the channel.
     * We intercept STOMP CONNECT commands to authenticate the user.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Wrap the message to access STOMP headers
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class);

        // Only process CONNECT commands (initial connection handshake)
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract Authorization header from STOMP frame
            // Client sends: CONNECT\nAuthorization:Bearer <token>\n\n
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7); // Remove "Bearer " prefix

                try {
                    // Extract username (email) from JWT
                    String userEmail = jwtUtils.extractUsername(jwt);

                    if (userEmail != null) {
                        // Load user details from database
                        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                        // Validate the token
                        if (jwtUtils.isTokenValid(jwt, userDetails)) {
                            // Create authentication token
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                            // Set the user on this WebSocket session
                            // This is the KEY step - it allows convertAndSendToUser() to work
                            accessor.setUser(authToken);

                            log.debug("WebSocket authenticated for user: {}", userEmail);
                        } else {
                            log.warn("Invalid JWT token for WebSocket connection");
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to authenticate WebSocket connection: {}", e.getMessage());
                }
            } else {
                log.warn("WebSocket CONNECT without Authorization header");
            }
        }

        return message;
    }
}
