package distove.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotifyNewMessageEvent implements Event {
    private Long channelId;
}
