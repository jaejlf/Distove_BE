package distove.chat.dto.response;

import distove.chat.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;

import static distove.chat.enumerate.MessageType.*;

@Getter
@Builder
public class TypedUserResponse {

    private MessageType type;
    private String content;

    public static TypedUserResponse of(String nickname) {
        return TypedUserResponse.builder()
                .type(TYPING)
                .content(nickname)
                .build();
    }

}
