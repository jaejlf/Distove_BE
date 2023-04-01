package distove.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotifyUnreadsEvent implements Event {
    private Long userId;
    private Long serverId;
}
