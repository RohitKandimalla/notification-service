package gov.cms.madie.notification_service.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReply {

  private String id;

  private String author;

  private String text;

  private Instant createdAt;
}

