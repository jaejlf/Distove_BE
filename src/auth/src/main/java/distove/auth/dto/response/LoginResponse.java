package distove.auth.dto.response;

import distove.auth.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private UserResponse user;

    public static LoginResponse of(String accessToken, User user) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(UserResponse.of(user))
                .build();
    }

}
