package gov.cms.madie.notification_service.service;

import gov.cms.madie.notification_service.model.CommentReply;
import gov.cms.madie.notification_service.model.ReviewComment;
import gov.cms.madie.notification_service.repository.ReviewCommentRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewCommentService {

  private final ReviewCommentRepository reviewCommentRepository;

  public List<ReviewComment> getCommentsByMeasureId(String measureId) {
    log.info("Fetching comments for measureId: {}", measureId);
    return reviewCommentRepository.findAllByMeasureIdOrderByCreatedAtAsc(measureId);
  }

  public ReviewComment createComment(ReviewComment comment) {
    comment.setCreatedAt(Instant.now());
    comment.setResolved(false);
    if (comment.getReplies() == null) {
      comment.setReplies(new ArrayList<>());
    }
    log.info(
        "Creating comment for measureId: {} by author: {}",
        comment.getMeasureId(),
        comment.getAuthor());
    return reviewCommentRepository.save(comment);
  }

  public ReviewComment addReply(String id, CommentReply reply, String username) {
    ReviewComment comment =
        reviewCommentRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comment not found with id: " + id));

    reply.setCreatedAt(Instant.now());
    reply.setAuthor(username);
    comment.getReplies().add(reply);

    log.info(
        "Adding reply to comment [{}] by user: {}",
        id,
        username);
    return reviewCommentRepository.save(comment);
  }

  public void deleteComment(String id, String username) {
    ReviewComment comment =
        reviewCommentRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Comment not found with id: " + id));

    if (!comment.getAuthor().equalsIgnoreCase(username)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "Only the author can delete this comment");
    }

    log.info("Deleting comment [{}] by author: {}", id, username);
    reviewCommentRepository.deleteById(id);
  }
}

