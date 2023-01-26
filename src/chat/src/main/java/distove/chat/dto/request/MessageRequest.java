package distove.chat.dto.request;

import distove.chat.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    private MessageType type;
    private String messageId;
    private String content;
    private String parentId;
    private String replyName; // reply 생성 시에만 필요

    public MessageRequest(MessageType type, String content, String parentId) {
        this.type = type;
        this.content = content;
        this.parentId = parentId;
    }

}
