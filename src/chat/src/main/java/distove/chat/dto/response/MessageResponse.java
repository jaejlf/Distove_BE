package distove.chat.dto.response;

import distove.chat.entity.Message;
import distove.chat.enumerate.MessageType;
import distove.chat.web.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class MessageResponse {

    private String id;
    private MessageType type;
    private String content;
    private LocalDateTime createdAt;
    private UserResponse writer;
    private Boolean hasAuthorized;

    public static MessageResponse of(Message message, UserResponse writer, Long userId) {
        return MessageResponse.builder()
                .id(message.getId())
                .type(message.getType())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .writer(writer)
                .hasAuthorized(Objects.equals(writer.getId(), userId))
                .build();
    }

}
