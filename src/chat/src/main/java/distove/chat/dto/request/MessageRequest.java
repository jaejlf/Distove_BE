package distove.chat.dto.request;

import distove.chat.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static distove.chat.enumerate.MessageType.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    private MessageType type;
    private MessageStatus status;
    private String messageId;
    private String content;
    private String parentId;
    private String replyName; // reply 최초 생성 시에만 필요

}
