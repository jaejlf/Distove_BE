package distove.chat.dto.response;

import distove.chat.enumerate.MessageType.MessageStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static distove.chat.enumerate.MessageType.MessageStatus.REACTED;


@Builder
@Getter
public class ReactionMessageResponse {
    private String messageId;
    private MessageStatus status;
    private List<ReactionResponse> reactions;

    public static ReactionMessageResponse newReactionMessageResponse(String messageId, List<ReactionResponse> reactions){
        return ReactionMessageResponse.builder()
                .messageId(messageId)
                .reactions(reactions)
                .status(REACTED)
                .build();
    }

}