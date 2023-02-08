package distove.chat.repository;

import distove.chat.entity.EventFail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventFailRepository extends MongoRepository<EventFail, String> {
}
