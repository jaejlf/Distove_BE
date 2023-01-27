package distove.auth.dto.request;

import lombok.Getter;

@Getter
public class SignUpRequest {
    private String email;

    private String password;

    private String nickname;

    private String profileImgUrl;
}