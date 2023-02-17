package distove.chat.dto.response;

import distove.chat.web.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReactionResponse {
    private String emoji;
    private List<UserResponse> users;

    public static ReactionResponse newReactionResponse(String emoji, List<UserResponse> users){
        return ReactionResponse.builder()
                .emoji(emoji)
                .users(users)
                .build();
    }

}
