package distove.voice.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VideoInfo {
    private Boolean isCameraOn;
    private Boolean isMicOn;

    public static VideoInfo of(Boolean isCameraOn, Boolean isMicOn){
        return  VideoInfo.builder()
                .isCameraOn(isCameraOn)
                .isMicOn(isMicOn)
                .build();
    }
}
