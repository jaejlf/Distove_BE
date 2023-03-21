package distove.presence.enumerate;

import distove.presence.entity.Presence;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceType {

    OFFLINE(new Presence(PresenceStatus.OFFLINE,"오프라인")),
    ONLINE(new Presence(PresenceStatus.ONLINE,"온라인")),
    ONLINE_CALL(new Presence(PresenceStatus.ONLINE_CALL,"화상통화 중")),
    AWAY(new Presence(PresenceStatus.AWAY,"자리비움"));

    private final Presence presence;

}
