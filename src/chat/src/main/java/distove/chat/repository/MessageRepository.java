package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByChannelId(Long channelId);
    List<Message> findAllByChannelIdAndReplyInfoIsNotNull(Long channelId);
    Optional<Message> findById(String id);
}
