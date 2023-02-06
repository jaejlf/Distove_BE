package distove.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TokenResponse {

    private String accessToken;

    public static TokenResponse of(String accessToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
