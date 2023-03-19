package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import static distove.voice.enumerate.MessageType.PARTICIPANT_LEFT;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ParticipantLeftResponse {

    private final String type = PARTICIPANT_LEFT.getMessage();
    private final Long userId;

    public static ParticipantLeftResponse of(Long userId) {
        return ParticipantLeftResponse.builder()
                .userId(userId)
                .build();
    }

}
