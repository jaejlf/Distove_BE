package distove.chat.repository;

import distove.chat.entity.Connection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends MongoRepository<Connection, String> {
    Optional<Connection> findByChannelId(Long channelId);
    void deleteAllByChannelId(Long channelId);
    List<Connection> findAllByServerId(Long serverId);
}
