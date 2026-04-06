package gov.cms.madie.notification_service.client;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

  private final RestTemplate userServiceRestTemplate;

  @Value("${madie.user-service.base-url}")
  private String userServiceBaseUrl;

  public List<String> getAllActiveUserHarpIds() {
    try {
      String url = userServiceBaseUrl + "/api/admin/users/last-login";
      log.info("Fetching all active MADiE user harpIds from user-service");

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

    } catch (Exception e) {
      log.error("Failed to fetch users from user-service: {}", e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  record UserLoginDto(String harpId, String status) {}
}
