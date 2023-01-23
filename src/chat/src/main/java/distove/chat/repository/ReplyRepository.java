package distove.chat.repository;

import distove.chat.entity.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReplyRepository extends MongoRepository<Reply, String> {
}
