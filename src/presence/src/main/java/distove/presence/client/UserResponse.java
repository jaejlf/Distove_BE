package distove.presence.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor // 더미 유저 생성을 위한 생성자
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String nickname;
    private String profileImgUrl;
}
