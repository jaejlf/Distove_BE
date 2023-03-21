package distove.presence.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import distove.presence.client.dto.UserResponse;
import distove.presence.enumerate.PresenceType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceResponse {

    private Long userId;
    private String nickname;
    private String profileImgUrl;
    private PresenceType presenceType;

    public static PresenceResponse of(UserResponse user, PresenceType presenceType) {
        return PresenceResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .presenceType(presenceType)
                .build();
    }

    public static PresenceResponse update(Long userId, PresenceType presenceType) {
        return PresenceResponse.builder()
                .userId(userId)
                .presenceType(presenceType)
                .build();
    }

}
