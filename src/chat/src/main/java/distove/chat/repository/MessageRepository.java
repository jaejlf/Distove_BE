package distove.chat.repository;

import distove.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    Page<Message> findAllByChannelId(Long channelId, Pageable pageable);
    List<Message> findAllByChannelIdAndReplyInfoIsNotNull(Long channelId);
    Optional<Message> findById(String id);
    Optional<Message> findByIdAndReplyInfoIsNotNull(String id);
}
