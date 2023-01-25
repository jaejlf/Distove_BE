package distove.chat.repository;

import distove.chat.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReplyRepository extends MongoRepository<Reply, String> {
    Page<Reply> findAllByParentId(String parentId, Pageable pageable);
}
