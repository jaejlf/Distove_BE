package distove.chat.dto.request;

import distove.chat.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;

import static distove.chat.enumerate.MessageType.*;
import static distove.chat.enumerate.MessageType.MessageStatus.CREATED;

@Getter
@Builder
public class MessageRequest {

    private MessageType type;
    private MessageStatus status;
    private String messageId;
    private String content;
    private String parentId;
    private String threadName; // 스레드 최초 생성 시에만 필요
    private String emoji;

    public static MessageRequest ofFileType(MessageType type, String content, String parentId) {
        return MessageRequest.builder()
                .type(type)
                .status(CREATED)
                .content(content)
                .parentId(parentId)
                .build();
    }

    public static MessageRequest ofWelcome(String content) {
        return MessageRequest.builder()
                .type(WELCOME)
                .status(CREATED)
                .content(content)
                .build();
    }

}
