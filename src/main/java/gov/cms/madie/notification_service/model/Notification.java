package gov.cms.madie.notification_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

  @Id private String id;

  /** harpId of the recipient */
  private String userId;

  /** Human-readable message, e.g. "Edwin updated Population Criteria for CMS123" */
  private String message;

  /**
   * Optional relative MADiE path for the action button in the notification panel, e.g.
   * "/measures/abc123/edit/cql". Null when not applicable to a row.
   */
  private String additionalLink;

  /** false = blue background in the notification panel row */
  @JsonProperty("isRead")
  private boolean isRead;

  /** false = counted toward the unread badge on the notification icon */
  @JsonProperty("isSeen")
  private boolean isSeen;

  private Instant createdAt;
}
