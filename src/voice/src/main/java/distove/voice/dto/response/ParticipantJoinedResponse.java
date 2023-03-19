package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.client.dto.UserResponse;
import distove.voice.entity.VideoSetting;
import lombok.Builder;

import static distove.voice.enumerate.MessageType.PARTICIPANT_JOINED;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ParticipantJoinedResponse {

    private final String type = PARTICIPANT_JOINED.getMessage();
    private final ParticipantResponse participant;

    public static ParticipantJoinedResponse of(UserResponse user, VideoSetting videoSetting) {
        ParticipantResponse participantResponse = ParticipantResponse.of(user, videoSetting);
        return ParticipantJoinedResponse.builder()
                .participant(participantResponse)
                .build();
    }

}
