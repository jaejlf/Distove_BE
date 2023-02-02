package distove.auth.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignUpRequest {
    private String email;

    private String password;

    private String nickname;

    private MultipartFile profileImg;
}