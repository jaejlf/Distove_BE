package distove.chat.dto.request;

import distove.chat.enumerate.MessageType;
import lombok.Getter;

import static distove.chat.enumerate.MessageType.MessageStatus;

@Getter
public class MessageRequest {
    private MessageType type;
    private MessageStatus status;
    private String messageId;
    private String content;
    private String parentId;
    private String replyName; // reply 최초 생성 시에만 필요
}
