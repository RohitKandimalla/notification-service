package gov.cms.madie.notification_service.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

  private final JwtDecoder jwtDecoder;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    // Only validate on CONNECT — all other STOMP frames (SUBSCRIBE, MESSAGE) pass through freely
    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authHeader = accessor.getFirstNativeHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        log.warn("WebSocket CONNECT rejected: missing or malformed Authorization header");
        throw new BadCredentialsException("Missing Bearer token in STOMP CONNECT frame");
      }

      try {
        Jwt jwt = jwtDecoder.decode(authHeader.substring(7));
        JwtAuthenticationToken auth = (JwtAuthenticationToken) jwtAuthenticationConverter.convert(jwt);
        JwtAuthenticationToken lowercasedAuth = new JwtAuthenticationToken(
            jwt, auth.getAuthorities(), jwt.getSubject().toLowerCase());
        accessor.setUser(lowercasedAuth);
        log.info("WebSocket CONNECT authenticated for user: {}", accessor.getUser().getName());
      } catch (JwtException e) {
        log.warn("WebSocket CONNECT rejected: JWT validation failed - {}", e.getMessage());
        throw new BadCredentialsException("Invalid JWT token: " + e.getMessage(), e);
      }
    }

    return message;
  }
}
