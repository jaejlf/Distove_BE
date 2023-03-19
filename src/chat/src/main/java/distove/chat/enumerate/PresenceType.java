package distove.chat.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PresenceType {

    VOICE_ON("voiceOn"),
    VOICE_OFF("voiceOff"),
    CHAT("chat"),
    COMMUNITY("community");

    private final String type;

}
