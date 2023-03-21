package distove.voice.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceInfo {

    VOICE_ON("voiceOn"),
    VOICE_OFF("voiceOff"),
    CHAT("chat"),
    COMMUNITY("community");

    private final String type;

}
