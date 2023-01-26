package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    Page<Message> findAllByChannelIdAndParentIdIsNull(Long channelId, Pageable pageable);
    Page<Message> findAllByParentId(String parentId, Pageable pageable);
    List<Message> findAllByParentId(String parentId);
    List<Message> findAllByChannelIdAndReplyNameIsNotNull(Long channelId);
    void deleteAllByParentId(String parentId);
}
