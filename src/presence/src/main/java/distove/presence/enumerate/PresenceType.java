package distove.presence.enumerate;

import distove.presence.dto.Presence;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceType {
    OFFLINE(Presence.of(PresenceStatus.OFFLINE,"오프라인")),
    ONLINE(Presence.of(PresenceStatus.ONLINE,"온라인")),
    ONLINE_CALL(Presence.of(PresenceStatus.ONLINE_CALL,"화상통화 중")),
    AWAY(Presence.of(PresenceStatus.AWAY,"자리비움"));
    private final Presence presence;

}
