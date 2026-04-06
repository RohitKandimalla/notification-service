package gov.cms.madie.notification_service.service;

import gov.cms.madie.notification_service.model.Notification;
import gov.cms.madie.notification_service.repository.NotificationRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public List<Notification> getNotificationsByUserId(String userId) {
    log.info("Fetching notifications for user: {}", userId);
    return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
  }

  public Notification createNotification(Notification notification) {
    notification.setCreatedAt(Instant.now());
    notification.setRead(false);
    notification.setSeen(false);
    log.info("Creating notification for user: {}", notification.getUserId());
    return notificationRepository.save(notification);
  }

  public void deleteNotification(String id) {
    log.info("Deleting notification with id: {}", id);
    notificationRepository.deleteById(id);
  }
}
