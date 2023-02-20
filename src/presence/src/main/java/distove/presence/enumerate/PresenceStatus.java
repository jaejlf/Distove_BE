package distove.presence.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceStatus {
    OFFLINE("offline"),
    ONLINE("online"),
    ONLINE_CALL("calling"),
    AWAY("away");
    private final String status;

}
