package distove.community.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelType {
    CHAT(1), VOICE(2);
    private final Integer code;
}


