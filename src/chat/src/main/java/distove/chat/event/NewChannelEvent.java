package distove.chat.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewChannelEvent implements Event {

    private Long userId;
    private Long channelId;

    public static NewChannelEvent of(Long userId, Long channelId) {
        return NewChannelEvent.builder()
                .userId(userId)
                .channelId(channelId)
                .build();
    }

}