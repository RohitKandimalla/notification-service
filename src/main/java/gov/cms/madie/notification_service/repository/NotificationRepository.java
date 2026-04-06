package gov.cms.madie.notification_service.repository;

import gov.cms.madie.notification_service.model.Notification;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

  List<Notification> findAllByUserIdOrderByCreatedAtDesc(String userId);
}
