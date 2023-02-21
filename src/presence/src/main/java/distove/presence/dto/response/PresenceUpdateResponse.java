package distove.presence.dto.response;

import distove.presence.dto.Presence;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresenceUpdateResponse {
    private Long userId;
    private Presence presence;
    public static PresenceUpdateResponse of(Long userId, Presence presence){
        return PresenceUpdateResponse.builder()
                .userId(userId)
                .presence(presence)
                .build();
    }
}
