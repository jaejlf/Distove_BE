package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.entity.VideoSetting;
import lombok.Builder;

import static distove.voice.enumerate.MessageType.UPDATE_SETTING;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UpdateSettingResponse {

    private final String type = UPDATE_SETTING.getMessage();
    private Long userId;
    VideoSetting videoSetting;

    public static UpdateSettingResponse of(Long userId, VideoSetting videoSetting) {
        return UpdateSettingResponse.builder()
                .userId(userId)
                .videoSetting(videoSetting)
                .build();
    }

}
