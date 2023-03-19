package distove.presence.enumerate;

import distove.presence.entity.Presence;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceType {

    OFFLINE(Presence.newPresence(PresenceStatus.OFFLINE,"오프라인")),
    ONLINE(Presence.newPresence(PresenceStatus.ONLINE,"온라인")),
    ONLINE_CALL(Presence.newPresence(PresenceStatus.ONLINE_CALL,"화상통화 중")),
    AWAY(Presence.newPresence(PresenceStatus.AWAY,"자리비움"));

    private final Presence presence;

}
