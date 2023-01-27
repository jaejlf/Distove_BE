package distove.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LogoutResponse {
    private String token;

    public static LogoutResponse of(String token) {
        return LogoutResponse.builder()
                .token(token)
                .build();
    }
}