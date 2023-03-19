package distove.chat.dto.request;

import lombok.Getter;

@Getter
public class ReactionRequest {
    private String messageId;
    private String emoji;
}
