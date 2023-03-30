package distove.chat.dto.response;

import distove.chat.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;

import static distove.chat.enumerate.MessageType.*;
import static distove.chat.enumerate.MessageType.MessageStatus.*;

@Getter
@Builder
public class TypingUserResponse {

    private MessageType type;
    private MessageStatus status;
    private String content;

    public static TypingUserResponse of(String nickname) {
        return TypingUserResponse.builder()
                .type(TEXT)
                .status(TYPING)
                .content(nickname)
                .build();
    }

}
