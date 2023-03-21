package distove.presence.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceType {

    OFFLINE("offline", "오프라인"),
    ONLINE("online", "온라인"),
    VOICE_ON("voiceOn", "화상통화 중"),
    AWAY("away", "자리비움");

    private final String status;
    private final String description;

}
