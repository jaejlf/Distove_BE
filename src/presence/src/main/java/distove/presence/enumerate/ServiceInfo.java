package distove.presence.enumerate;

import distove.presence.exception.DistoveException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static distove.presence.exception.ErrorCode.SERVICE_INFO_TYPE_ERROR;

@Getter
@AllArgsConstructor
public enum ServiceInfo {

    VOICE_ON("voiceOn"),
    VOICE_OFF("voiceOff"),
    CHAT("chat"),
    COMMUNITY("community"),
    CONNECT("connect"),
    DISCONNECT("disconnect");

    private final String type;

    public static ServiceInfo getServiceInfo(String type) {
        return Arrays.stream(ServiceInfo.values())
                .filter(x -> x.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new DistoveException(SERVICE_INFO_TYPE_ERROR));
    }

}
