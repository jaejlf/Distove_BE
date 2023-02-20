package distove.auth.dto.response;

import distove.auth.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private UserResponse user;

    public static LoginResponse of(String accessToken, UserResponse user) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }

}

