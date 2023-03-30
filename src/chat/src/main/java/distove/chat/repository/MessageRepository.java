package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String>, CustomMessageRepository {

    Optional<Message> findByIdAndChannelId(String messageId, Long channelId);

    List<Message> findAllByParentId(String parentId);

    List<Message> findAllByChannelIdAndThreadNameIsNotNull(Long channelId);

    void deleteAllByParentId(String parentId);

    void deleteAllByChannelId(Long channelId);

}
