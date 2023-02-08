package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.enumerate.MessageType;
import distove.voice.web.UserResponse;
import lombok.Builder;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NewParticipantArrivedResponse {
    private final String type = MessageType.NEW_PARTICIPANT_ARRIVED.getType();
    private final UserResponse user;

    public static NewParticipantArrivedResponse newNewParticipantArrivedResponse(UserResponse user) {
        return NewParticipantArrivedResponse.builder()
                .user(user)
                .build();
    }
}
