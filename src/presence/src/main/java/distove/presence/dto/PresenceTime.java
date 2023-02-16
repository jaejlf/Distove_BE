package distove.presence.dto;

import distove.presence.enumerate.PresenceStatus;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class PresenceTime {
    private Presence presence;
    private Timestamp activeAt;

    public static PresenceTime newPresenceTime(Presence presence){
        return PresenceTime.builder()
                .presence(presence)
                .activeAt(new Timestamp(System.currentTimeMillis())).build();
    }
}
