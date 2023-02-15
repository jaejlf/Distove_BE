package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String>, CustomMessageRepository {
    List<Message> findAllByParentId(String parentId);
    List<Message> findAllByChannelIdAndReplyNameIsNotNull(Long channelId);
    void deleteAllByParentId(String parentId);
    void deleteAllByChannelId(Long channelId);
}
