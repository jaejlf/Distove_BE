package distove.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import distove.chat.entity.Message;
import distove.chat.entity.ReplyInfo;
import distove.chat.enumerate.MessageType;
import distove.chat.web.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

import static distove.chat.enumerate.MessageType.isNotiMessage;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {

    private String id;
    private MessageType type;
    private String content;
    private LocalDateTime createdAt;
    private UserResponse writer;
    private Boolean hasAuthorized;
    private ReplyInfo replyInfo;

    public static MessageResponse of(Message message, UserResponse writer, Long userId) {
        if (isNotiMessage(message.getType())) {
            return MessageResponse.builder()
                    .id(message.getId())
                    .type(message.getType())
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .hasAuthorized(false)
                    .build();
        } else {
            return MessageResponse.builder()
                    .id(message.getId())
                    .type(message.getType())
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .writer(writer)
                    .hasAuthorized(Objects.equals(writer.getId(), userId))
                    .replyInfo(message.getReplyInfo())
                    .build();
        }
    }

}
