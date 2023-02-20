package distove.presence.dto.response;

import distove.presence.dto.Presence;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class PresenceResponse {
    private Long id;
    private String nickname;
    private String profileImgUrl;
    private Presence presence;
    public static PresenceResponse of(Long id, String nickname,String profileImgUrl,Presence presence){
        return PresenceResponse.builder()
                .id(id)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .presence(presence)
                .build();
    }
}
