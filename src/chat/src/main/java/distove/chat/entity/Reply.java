package distove.chat.entity;

import lombok.Builder;
import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "reply")
public class Reply {

    @Id
    private String id;
    private String parentId;
    private Message message;

    public static Reply newReply(String parentId, Message message) {
        return Reply.builder()
                .parentId(parentId)
                .message(message)
                .build();
    }

}
