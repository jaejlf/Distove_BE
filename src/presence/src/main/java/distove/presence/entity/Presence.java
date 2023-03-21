package distove.presence.entity;

import distove.presence.enumerate.PresenceType;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class Presence {

    private final PresenceType presenceType;
    private final Timestamp activeAt;

    public Presence(PresenceType presenceType) {
        this.presenceType = presenceType;
        this.activeAt = new Timestamp(System.currentTimeMillis());
    }

}
