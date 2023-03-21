package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

import java.util.List;

import static distove.voice.enumerate.MessageType.EXISTING_PARTICIPANTS;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ExistingParticipantsResponse {

    private final String message = EXISTING_PARTICIPANTS.getMessage();
    private final List<ParticipantResponse> participants;

    public static ExistingParticipantsResponse of(List<ParticipantResponse> participants) {
        return ExistingParticipantsResponse.builder()
                .participants(participants)
                .build();
    }

}
