package distove.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DeleteChannelsEvent implements Event {
    private List<Long> channelIds;
}
