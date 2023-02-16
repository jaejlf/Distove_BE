package distove.presence.enumerate;

import distove.presence.dto.Presence;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceType {
    OFFLINE(Presence.ofOnlyStatus(PresenceStatus.OFFLINE)),
    ONLINE(Presence.ofOnlyStatus(PresenceStatus.ONLINE)),
    ONLINE_CALL(Presence.of(PresenceStatus.ONLINE,"화상통화 중")),
    AWAY(Presence.ofOnlyStatus(PresenceStatus.AWAY)),
    TYPING(Presence.ofOnlyStatus(PresenceStatus.TYPING));

    private final Presence presence;

}
