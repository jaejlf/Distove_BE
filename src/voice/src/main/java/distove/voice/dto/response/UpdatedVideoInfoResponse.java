package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.entity.VideoInfo;
import distove.voice.enumerate.MessageType;
import lombok.Builder;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UpdatedVideoInfoResponse {
    private final String type = MessageType.UPDATED_VIDEO_INFO.getType();
    private Long userId;
    VideoInfo videoInfo;

    public static UpdatedVideoInfoResponse of(Long userId,VideoInfo videoInfo){
        return UpdatedVideoInfoResponse.builder()
                .userId(userId)
                .videoInfo(videoInfo)
                .build();
    }
}
