package distove.presence.dto;

import distove.presence.enumerate.PresenceStatus;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class Presence {
    private PresenceStatus status;
    private String description;

    public static Presence of(PresenceStatus status,String description){
        return Presence.builder()
                .status(status)
                .description(description)
                .build();
    }
    public static Presence ofOnlyStatus(PresenceStatus status){
        return Presence.builder()
                .status(status)
                .build();
    }

}
