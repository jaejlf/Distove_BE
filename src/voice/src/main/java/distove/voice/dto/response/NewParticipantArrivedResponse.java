package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.entity.Participant;
import lombok.Builder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NewParticipantArrivedResponse {
    private final String type = "newParticipantArrived";
    private final Long userId;

    @Builder
    public NewParticipantArrivedResponse(Participant newParticipant) {
        this.userId = newParticipant.getUserId();
    }
}
