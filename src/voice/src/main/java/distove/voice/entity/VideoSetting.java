package distove.voice.entity;

import lombok.Getter;

@Getter
public class VideoSetting {

    private final Boolean isCameraOn;
    private final Boolean isMicOn;

    public VideoSetting(Boolean isCameraOn, Boolean isMicOn) {
        this.isCameraOn = isCameraOn;
        this.isMicOn = isMicOn;
    }

}
