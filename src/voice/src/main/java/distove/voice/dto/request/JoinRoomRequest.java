package distove.voice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)

public class JoinRoomRequest {
    private String type;
    private Long userId;
    private Long channelId;
    private Boolean isCameraOn = false;
    private Boolean isMicOn = false;
}
