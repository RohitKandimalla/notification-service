package gov.cms.madie.notification_service.controller;

import gov.cms.madie.notification_service.model.Notification;
import gov.cms.madie.notification_service.service.NotificationService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<Notification>> getNotifications(Principal principal) {
    String userId = principal.getName();
    log.info("User [{}] - Fetching all notifications", userId);
    return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
  }

  @PostMapping
  public ResponseEntity<List<Notification>> createNotification(
      @RequestBody List<Notification> notifications, Principal principal) {
    log.info("User [{}] - Creating {} notification(s)", principal.getName(), notifications.size());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(notificationService.createNotification(notifications));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNotification(@PathVariable String id, Principal principal) {
    log.info("User [{}] - Deleting notification with id: {}", principal.getName(), id);
    notificationService.deleteNotification(id);
    return ResponseEntity.noContent().build();
  }
}
