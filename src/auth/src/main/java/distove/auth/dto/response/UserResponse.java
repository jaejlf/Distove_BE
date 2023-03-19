package distove.auth.dto.response;

import distove.auth.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponse {

    private Long id;
    private String nickname;
    private String profileImgUrl;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

}
