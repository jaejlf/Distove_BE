package distove.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import distove.chat.entity.Message;
import distove.chat.entity.Reaction;
import distove.chat.enumerate.MessageType;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static distove.chat.enumerate.MessageType.*;

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
    private Boolean hasAuthorized;
    private ReplyInfoResponse replyInfo;
    private List<ReactionResponse> reactions;
    private String profileImgUrl;
    private String nickname;

    public static MessageResponse ofDefault(Message message, UserResponse writer, Long userId, List<ReactionResponse> reactions) {
        return MessageResponse.builder()
                .id(message.getId())
                .type(message.getType())
                .status(message.getStatus())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .writer(writer)
                .hasAuthorized(Objects.equals(writer.getId(), userId))
                .reactions(reactions)
                .build();
    }

    public static MessageResponse ofParent(Message message, UserResponse writer, Long userId, ReplyInfoResponse replyInfo, List<ReactionResponse> reactions) {
        return MessageResponse.builder()
                .id(message.getId())
                .type(message.getType())
                .status(message.getStatus())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .writer(writer)
                .replyInfo(replyInfo)
                .hasAuthorized(Objects.equals(writer.getId(), userId))
                .reactions(reactions)
                .build();
    }

    public static MessageResponse ofSearching(Message message, UserResponse writer) {
        return MessageResponse.builder()
                .id(message.getId())
                .type(message.getType())
                .status(message.getStatus())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .writer(writer)
                .build();
    }

}
