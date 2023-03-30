package distove.chat.entity;

import distove.chat.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static distove.chat.enumerate.MessageType.MessageStatus;
import static distove.chat.enumerate.MessageType.MessageStatus.CREATED;

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
    private List<Reaction> reactions;

    // 스레드 메시지
    private String threadName;
    private Long threadStarterId;
    private String parentId;

    public static Message newMessage(Long channelId, Long userId, MessageType type, String content, String parentId) {
        return Message.builder()
                .channelId(channelId)
                .userId(userId)
                .type(type)
                .status(CREATED)
                .content(content)
                .createdAt(LocalDateTime.now())
                .reactions(new ArrayList<>())
                .parentId(parentId)
                .build();
    }

    public void updateMessage(MessageStatus status, String content) {
        this.status = status;
        this.content = content;
    }

    public void createThread(String threadName, Long threadStarterId) {
        this.threadName = threadName;
        this.threadStarterId = threadStarterId;
    }

    public void updateReaction(List<Reaction> reactions) {
        this.reactions = reactions;
    }

}