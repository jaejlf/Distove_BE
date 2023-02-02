package distove.chat.dto.response;

import distove.chat.web.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReactionMessageResponse {
    private String messageId;
    private List<ReactionResponse> reactions;

    public static ReactionMessageResponse newReactionMessageResponse(String messageId, List<ReactionResponse> reactions){
        return ReactionMessageResponse.builder()
                .messageId(messageId)
                .reactions(reactions)
                .build();
    }

}