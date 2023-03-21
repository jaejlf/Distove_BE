package distove.presence.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePresenceEvent implements Event {
    private Long userId;
    private String type;
}
