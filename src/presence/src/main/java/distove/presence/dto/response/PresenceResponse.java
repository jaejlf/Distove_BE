package distove.presence.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import distove.presence.entity.Presence;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceResponse {

    private Long userId;
    private String nickname;
    private String profileImgUrl;
    private Presence presence;

    public static PresenceResponse of(Long userId, String nickname, String profileImgUrl, Presence presence) {
        return PresenceResponse.builder()
                .userId(userId)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .presence(presence)
                .build();
    }

    public static PresenceResponse update(Long userId, Presence presence) {
        return PresenceResponse.builder()
                .userId(userId)
                .presence(presence)
                .build();
    }

}
