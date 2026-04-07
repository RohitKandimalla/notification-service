package gov.cms.madie.notification_service.service;

import gov.cms.madie.notification_service.client.UserServiceClient;
import gov.cms.madie.notification_service.model.Notification;
import gov.cms.madie.notification_service.repository.NotificationRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final MongoTemplate mongoTemplate;
  private final UserServiceClient userServiceClient;

  public List<Notification> getNotificationsByUserId(String userId) {
    log.info("Fetching notifications for user: {}", userId);
    return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
  }

  public List<Notification> createNotification(Notification request) {
    List<String> userIds;

    if (CollectionUtils.isEmpty(request.getUserIds())) {
      log.info("No userIds provided — fetching all active users from user-service for global notification");
      userIds = userServiceClient.getAllActiveUserHarpIds();
      log.info("Fetched {} active user(s) from user-service", userIds.size());
    } else {
      log.info("Creating targeted notification for {} provided user(s)", request.getUserIds().size());
      userIds = request.getUserIds();
    }

    List<Notification> notifications = userIds.stream()
        .map(userId -> Notification.builder()
            .userId(userId)
            .message(request.getMessage())
            .additionalLink(request.getAdditionalLink())
            .isRead(false)
            .isSeen(false)
            .createdAt(Instant.now())
            .build())
        .toList();
    return notificationRepository.saveAll(notifications);
  }

  public void markSeen(List<String> ids) {
    log.info("Marking {} notification(s) as seen", ids.size());
    mongoTemplate.updateMulti(
        Query.query(Criteria.where("_id").in(ids)),
        Update.update("isSeen", true),
        Notification.class);
  }

  public void markRead(String id) {
    log.info("Marking notification [{}] as read", id);
    mongoTemplate.updateFirst(
        Query.query(Criteria.where("_id").is(id)),
        Update.update("isRead", true),
        Notification.class);
  }

  public void deleteNotification(String id) {
    log.info("Deleting notification with id: {}", id);
    notificationRepository.deleteById(id);
  }

  public long deleteNotifications(List<String> ids, String userId) {
    log.info("Deleting {} notification(s) for user: {}", ids.size(), userId);
    var result = mongoTemplate.remove(
        Query.query(Criteria.where("_id").in(ids).and("userId").is(userId)),
        Notification.class);
    log.info("Deleted {} notification(s) for user: {}", result.getDeletedCount(), userId);
    return result.getDeletedCount();
  }
}
