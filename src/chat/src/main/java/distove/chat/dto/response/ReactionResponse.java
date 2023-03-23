package distove.chat.dto.response;

import distove.chat.client.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReactionResponse {

    private String emoji;
    private List<UserResponse> users;

    public static ReactionResponse of(String emoji, List<UserResponse> users) {
        return ReactionResponse.builder()
                .emoji(emoji)
                .users(users)
                .build();
    }

}
