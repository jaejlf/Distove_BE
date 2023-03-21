package distove.presence.entity;

import distove.presence.enumerate.PresenceStatus;
import lombok.Getter;

@Getter
public class Presence {

    private final PresenceStatus status;
    private final String description;

    public Presence(PresenceStatus status, String description) {
        this.status = status;
        this.description = description;
    }

}
