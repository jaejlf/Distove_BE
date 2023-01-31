package distove.auth.dto.request;

import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Getter
public class UpdateNicknameRequest {
    private String token;

    private String nickname;
}
