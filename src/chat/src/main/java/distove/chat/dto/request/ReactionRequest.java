package distove.chat.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReactionRequest {
    private String messageId;
    private String emoji;


    public static ReactionRequest newReactionRequest(String messageId, String emoji) {
        return ReactionRequest.builder()
                .messageId(messageId)
                .emoji(emoji)
                .build();
    }
}
