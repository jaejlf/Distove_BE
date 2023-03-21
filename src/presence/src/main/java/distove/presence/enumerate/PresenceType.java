package distove.presence.enumerate;

import distove.presence.exception.DistoveException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static distove.presence.exception.ErrorCode.PRESENCE_TYPE_ERROR;

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

    public static PresenceType getPresenceType(String type) {
        return Arrays.stream(PresenceType.values())
                .filter(x -> x.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new DistoveException(PRESENCE_TYPE_ERROR));
    }

}
