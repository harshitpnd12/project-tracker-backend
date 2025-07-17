package com.projectmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Important: Add SockJS fallback support for client compatibility
        registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("http://localhost:5713",
        "https://project-tracker-two-xi.vercel.app")
        .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Clients will send messages to destinations that start with "/app"
        registry.setApplicationDestinationPrefixes("/app");

        // Clients will subscribe to these broker topics
        registry.enableSimpleBroker("/topic", "/queue", "/chat");

        // For one-to-one (private) messaging
        registry.setUserDestinationPrefix("/user");
    }
}
