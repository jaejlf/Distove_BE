package distove.presence.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceStatus {
    OFFLINE("offline"),
    ONLINE("online"),
    AWAY("away"),
    TYPING("typing");
    private final String status;

}
