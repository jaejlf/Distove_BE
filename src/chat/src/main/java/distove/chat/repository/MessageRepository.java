package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByParentId(String parentId);
    List<Message> findAllByChannelIdAndReplyNameIsNotNull(Long channelId);
    void deleteAllByParentId(String parentId);
    void deleteAllByChannelId(Long channelId);

    @Query(value = "{ 'channelId' : ?0, 'parentId' : null }", sort = "{ 'createdAt' :  -1 }")
    Page<Message> findAllParentByChannelId(Long channelId, Pageable pageable);

    @Query(value = "{ 'parentId' : ?0 }", sort = "{ 'createdAt' :  -1 }")
    Page<Message> findAllByChildByParentId(String parentId, Pageable pageable);

    @Query(value = "{ 'channelId' : ?0, 'createdAt' : { '$gt' : ?1 } }")
    List<Message> findUnreadMessage(Long channelId, LocalDateTime latestConnectedAt);
}
