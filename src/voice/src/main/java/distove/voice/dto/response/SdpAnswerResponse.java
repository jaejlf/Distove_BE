package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import static distove.voice.enumerate.MessageType.SDP_ANSWER;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SdpAnswerResponse {

    private final String type = SDP_ANSWER.getMessage();
    private final Long userId;
    private final String sdpAnswer;

    public static SdpAnswerResponse of(Long userId, String sdpAnswer) {
        return SdpAnswerResponse.builder()
                .userId(userId)
                .sdpAnswer(sdpAnswer)
                .build();
    }

}
