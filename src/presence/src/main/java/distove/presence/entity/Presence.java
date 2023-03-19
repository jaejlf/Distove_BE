package distove.presence.entity;

import distove.presence.enumerate.PresenceStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Presence {

    private PresenceStatus status;
    private String description;

    public static Presence newPresence(PresenceStatus status, String description){
        return Presence.builder()
                .status(status)
                .description(description)
                .build();
    }

}
