package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class SdpAnswerResponse {
    private final String type = "sdpAnswer";
    private final Long userId;
    private final String sdpAnswer;

    @Builder

    public SdpAnswerResponse(Long userId, String sdpAnswer) {
        this.userId = userId;
        this.sdpAnswer = sdpAnswer;
    }

}
