package distove.voice.dto.response;

import distove.voice.client.dto.UserResponse;
import distove.voice.entity.VideoSetting;
import lombok.Builder;

@Builder
public class ParticipantResponse {

    private Long id;
    private String nickname;
    private String profileImgUrl;
    private VideoSetting videoSetting;

    public static ParticipantResponse of(UserResponse user, VideoSetting videoSetting) {
        return ParticipantResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .videoSetting(videoSetting)
                .build();
    }

}
