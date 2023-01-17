package distove.chat.repository;

import distove.chat.entity.Connection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectionRepository extends MongoRepository<Connection, String> {
    Connection findByChannelId(Long channelId);
}
