package distove.voice.dto.request;

import lombok.Getter;

@Getter
public class JoinRoomRequest {
    private String type;
    private Long userId;
    private Long channelId;
}
