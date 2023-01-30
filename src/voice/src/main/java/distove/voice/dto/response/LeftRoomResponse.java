package distove.voice.dto.response;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.entity.Participant;
import lombok.Builder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class LeftRoomResponse {
    private final String type = "participantLeft";
    private final Long userId;

    @Builder

    public LeftRoomResponse(Participant participant) {
        this.userId = participant.getUserId();
    }
}
