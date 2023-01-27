package distove.chat.entity;

import distove.chat.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@Document(collection = "message")
public class Message {

    @Id
    private String id;
    private Long channelId;
    private Long userId;
    private MessageType type;
    private String content;
    private LocalDateTime createdAt;
    private String replyName;
    private Long stUserId;
    private String parentId;

    public static Message newMessage(Long channelId, Long userId, MessageType type, String content) {
        return Message.builder()
                .channelId(channelId)
                .userId(userId)
                .type(type)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Message newReply(Long channelId, Long userId, MessageType type, String content, String parentId) {
        return Message.builder()
                .channelId(channelId)
                .userId(userId)
                .type(type)
                .content(content)
                .parentId(parentId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateMessage(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

    public void addReplyInfo(String replyName, Long stUserId) {
        this.replyName = replyName;
        this.stUserId = stUserId;
    }

}