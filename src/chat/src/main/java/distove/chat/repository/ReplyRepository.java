package distove.chat.repository;

import distove.chat.entity.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReplyRepository extends MongoRepository<Reply, String> {
    List<Reply> findAllByParentId(String parentId);
}
