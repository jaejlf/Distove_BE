package distove.chat.dto.response;

import distove.chat.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TypedUserResponse {

    private MessageType type;
    private String nickname;

    public static TypedUserResponse of(MessageType type, String nickname) {
        return TypedUserResponse.builder()
                .type(type)
                .nickname(nickname)
                .build();
    }

}
