package gov.cms.madie.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private static final String[] ALLOWED_ORIGINS = {
    "http://localhost:9000",
    "https://dev-madie.hcqis.org",
    "https://test-madie.hcqis.org",
    "https://impl-madie.hcqis.org",
    "https://dev.madie.internal.cms.gov",
    "https://test.madie.internal.cms.gov",
    "https://impl.madie.internal.cms.gov",
    "https://madie.cms.gov"
  };

  // getName() returns jwt.getSubject() = harpId, which convertAndSendToUser uses to route to the right session
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    return new JwtAuthenticationConverter();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .setAllowedOrigins(ALLOWED_ORIGINS)
        .withSockJS();
  }
}
