package distove.voice.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceType {

    ONLINE("online", "온라인"),
    OFFLINE("offline", "오프라인"),
    VOICE_ON("voiceOn", "화상통화 중"),
    VOICE_OFF("voiceOff", "화상통화 종료"),
    AWAY("away", "자리비움");

    private final String type;
    private final String description;

}
