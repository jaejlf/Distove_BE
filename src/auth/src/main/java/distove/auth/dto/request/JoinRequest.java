package distove.auth.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class JoinRequest {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @NotNull
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @NotNull
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @NotNull
    private String nickname;

    private MultipartFile profileImg;
}