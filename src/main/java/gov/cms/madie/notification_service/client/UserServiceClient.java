package gov.cms.madie.notification_service.client;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

  private final RestTemplate userServiceRestTemplate;

  @Value("${madie.user-service.base-url}")
  private String userServiceBaseUrl;

  public List<String> getAllActiveUserHarpIds() {
    String url = userServiceBaseUrl + "/api/admin/users/last-login";
    log.info("Fetching all active MADiE user harpIds from user-service");

    try {
      List<UserLoginDto> users =
          userServiceRestTemplate
              .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserLoginDto>>() {})
              .getBody();

      if (users == null) {
        return Collections.emptyList();
      }

      List<String> harpIds = users.stream()
          .filter(u -> "ACTIVE".equals(u.status()))
          .map(UserLoginDto::harpId)
          .filter(harpId -> harpId != null && !harpId.isBlank())
          .toList();

      log.info("Retrieved {} active harpIds from user-service", harpIds.size());
      return harpIds;

    } catch (HttpClientErrorException e) {
      log.error("User-service returned {} when fetching all active users", e.getStatusCode());
      throw new ResponseStatusException(e.getStatusCode(), "Failed to fetch users from user-service: " + e.getMessage());
    } catch (Exception e) {
      log.error("Failed to fetch users from user-service: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch users from user-service");
    }
  }

  record UserLoginDto(String harpId, String status) {}
}
