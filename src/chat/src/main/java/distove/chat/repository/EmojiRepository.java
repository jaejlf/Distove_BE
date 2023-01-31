package distove.chat.repository;

import distove.chat.entity.Emoji;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmojiRepository extends MongoRepository<Emoji, String> {
}
