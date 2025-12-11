package com.blassa.config;

import com.blassa.repository.UserRepository;
import com.blassa.security.JwtChannelInterceptor;
import com.blassa.security.JwtUtils;
import com.blassa.security.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                new JwtChannelInterceptor(jwtUtils,userDetailsService)
        );
    }
}
