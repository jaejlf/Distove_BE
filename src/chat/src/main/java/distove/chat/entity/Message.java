package distove.chat.entity;

import distove.chat.enumerate.MessageType;
import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "message")
public class Message {

    @Id
    private String id;
    private Long channelId;
    private Long userId;
    private MessageType type;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Message(Long channelId, Long userId, MessageType type, String content) {
        this.channelId = channelId;
        this.userId = userId;
        this.type = type;
        this.content = content;
    }

    public void updateMessage(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

}
