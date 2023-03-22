package distove.presence.entity;

import distove.presence.enumerate.PresenceType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Presence {

    private final PresenceType presenceType;
    private final LocalDateTime activeAt;

    public Presence(PresenceType presenceType) {
        this.presenceType = presenceType;
        this.activeAt = LocalDateTime.now();
    }

}
