package gov.cms.madie.notification_service.controller;

import gov.cms.madie.notification_service.model.CommentReply;
import gov.cms.madie.notification_service.model.ReviewComment;
import gov.cms.madie.notification_service.service.ReviewCommentService;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ReviewCommentController {

  private final ReviewCommentService reviewCommentService;

  @GetMapping
  public ResponseEntity<List<ReviewComment>> getComments(
      @RequestParam String measureId, Principal principal) {
    log.info("User [{}] - Fetching comments for measureId: {}", principal.getName(), measureId);
    return ResponseEntity.ok(reviewCommentService.getCommentsByMeasureId(measureId));
  }

  @PostMapping
  public ResponseEntity<ReviewComment> createComment(
      @RequestBody ReviewComment comment, Principal principal) {
    String username = principal.getName().toLowerCase(Locale.ROOT);
    comment.setAuthor(username);
    log.info(
        "User [{}] - Creating comment for measureId: {}",
        username,
        comment.getMeasureId());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(reviewCommentService.createComment(comment));
  }

  @PutMapping("/{id}/replies")
  public ResponseEntity<ReviewComment> addReply(
      @PathVariable String id,
      @RequestBody CommentReply reply,
      Principal principal) {
    String username = principal.getName().toLowerCase(Locale.ROOT);
    log.info("User [{}] - Adding reply to comment [{}]", username, id);
    return ResponseEntity.ok(reviewCommentService.addReply(id, reply, username));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteComment(
      @PathVariable String id, Principal principal) {
    String username = principal.getName().toLowerCase(Locale.ROOT);
    log.info("User [{}] - Deleting comment [{}]", username, id);
    reviewCommentService.deleteComment(id, username);
    return ResponseEntity.noContent().build();
  }
}

