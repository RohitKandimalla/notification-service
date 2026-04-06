package gov.cms.madie.notification_service.config.security;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityExceptionHandlers implements AccessDeniedHandler, AuthenticationEntryPoint {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    log.warn(
        "Access denied for path [{}]: {}",
        request.getRequestURI(),
        accessDeniedException.getMessage());
    writeErrorResponse(
        response,
        HttpServletResponse.SC_FORBIDDEN,
        "Forbidden",
        "User does not have the required role to access this resource",
        request.getRequestURI());
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    log.warn(
        "Unauthorized access for path [{}]: {}",
        request.getRequestURI(),
        authException.getMessage());
    writeErrorResponse(
        response,
        HttpServletResponse.SC_UNAUTHORIZED,
        "Unauthorized",
        "Authentication is required to access this resource",
        request.getRequestURI());
  }

  private void writeErrorResponse(
      HttpServletResponse response, int status, String error, String message, String path)
      throws IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", status);
    body.put("error", error);
    body.put("message", message);
    body.put("path", path);

    OBJECT_MAPPER.writeValue(response.getOutputStream(), body);
  }
}
