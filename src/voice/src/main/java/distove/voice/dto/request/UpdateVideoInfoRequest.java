package distove.voice.dto.request;

import lombok.Getter;

@Getter
public class UpdateVideoInfoRequest {
    private String type;
    private Boolean isCameraOn;
    private Boolean isMicOn;
}
