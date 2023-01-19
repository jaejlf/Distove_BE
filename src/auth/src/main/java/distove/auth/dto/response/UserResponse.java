package distove.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponse {

    private Long id;
    private String nickname;

    public static UserResponse of(Long id, String nickname) {
        return UserResponse.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }
}
