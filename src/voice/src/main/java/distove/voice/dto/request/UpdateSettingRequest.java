package distove.voice.dto.request;

import lombok.Getter;

@Getter
public class UpdateSettingRequest {
    private Boolean isCameraOn;
    private Boolean isMicOn;
}