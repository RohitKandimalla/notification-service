package gov.cms.madie.notification_service.repository;

import gov.cms.madie.notification_service.model.ReviewComment;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewCommentRepository extends MongoRepository<ReviewComment, String> {

  List<ReviewComment> findAllByMeasureIdOrderByCreatedAtAsc(String measureId);
}

