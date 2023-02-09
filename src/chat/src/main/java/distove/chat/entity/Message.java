package distove.chat.entity;

import distove.chat.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

import static distove.chat.enumerate.MessageType.MessageStatus;

@Getter
@Builder
@Document(collection = "message")
public class Message {

    @Id
    private String id;
    private Long channelId;
    private Long userId;
    private MessageType type;
    private MessageStatus status;
    private String content;
    private LocalDateTime createdAt;
    private String replyName;
    private Long stUserId;
    private String parentId;
    private List<Reaction> reactions;

    public static Message newMessage(Long channelId, Long userId, MessageType type, MessageStatus status, String content) {
        return Message.builder()
                .channelId(channelId)
                .userId(userId)
                .type(type)
                .status(status)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Message newReply(Long channelId, Long userId, MessageType type, MessageStatus status, String content, String parentId) {
        return Message.builder()
                .channelId(channelId)
                .userId(userId)
                .type(type)
                .status(status)
                .content(content)
                .parentId(parentId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateMessage(MessageStatus status, String content) {
        this.status = status;
        this.content = content;
    }

    public void addReplyInfo(String replyName, Long stUserId) {
        this.replyName = replyName;
        this.stUserId = stUserId;
    }

    public void updateReaction(List<Reaction> reactions) {
        this.reactions = reactions;
    }

}