package distove.presence.dto.response;

import distove.presence.entity.Presence;
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
