package distove.voice.dto.response;

import distove.voice.entity.VideoInfo;
import distove.voice.enumerate.MessageType;
import distove.voice.web.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserVideoResponse {
    private Long id;
    private String nickname;
    private String profileImgUrl;
    private VideoInfo videoInfo;

    public static UserVideoResponse of(Long id, String nickname, String profileImgUrl, VideoInfo videoInfo){
        return UserVideoResponse.builder()
                .id(id)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .videoInfo(videoInfo)
                .build();
    }
}
