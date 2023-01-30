package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.enumerate.MessageType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class SdpAnswerResponse {
    private final String type = MessageType.SDP_ANSWER.getType();
    private final Long userId;
    private final String sdpAnswer;

    public static SdpAnswerResponse newSdpAnswerResponse(Long userId, String sdpAnswer) {
        return SdpAnswerResponse.builder()
                .userId(userId)
                .sdpAnswer(sdpAnswer)
                .build();
    }

}
