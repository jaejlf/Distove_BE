package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByChannelId(Long channelId);

    @Query("{id:'?0'}")
    Optional<Message> findById(String id);

    void deleteById(String id);
}
