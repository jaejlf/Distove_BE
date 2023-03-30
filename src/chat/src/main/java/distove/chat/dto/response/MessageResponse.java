package distove.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import distove.chat.client.dto.UserResponse;
import distove.chat.entity.Message;
import distove.chat.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static distove.chat.enumerate.MessageType.MessageStatus;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {

    private String id;
    private MessageType type;
    private MessageStatus status;
    private String content;
    private LocalDateTime createdAt;
    private UserResponse writer;
    private boolean hasAuthorized;
    private List<ReactionResponse> reactions;
    private ThreadInfoResponse threadInfo;

    public static MessageResponse of(Message message, UserResponse writer, Long userId, List<ReactionResponse> reactions, Optional<ThreadInfoResponse> threadInfo) {
        MessageResponseBuilder builder = MessageResponse.builder()
                .id(message.getId())
                .type(message.getType())
                .status(message.getStatus())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .writer(writer)
                .hasAuthorized(Objects.equals(writer.getId(), userId))
                .reactions(reactions);

        threadInfo.ifPresent(builder::threadInfo);

        return builder.build();
    }

}
