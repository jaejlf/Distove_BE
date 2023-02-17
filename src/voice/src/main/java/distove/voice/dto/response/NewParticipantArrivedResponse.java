package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.enumerate.MessageType;
import distove.voice.web.UserResponse;
import lombok.Builder;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NewParticipantArrivedResponse {
    private final String type = MessageType.NEW_PARTICIPANT_ARRIVED.getType();
    private final UserVideoResponse user;

    public static NewParticipantArrivedResponse newNewParticipantArrivedResponse(UserVideoResponse user) {
        return NewParticipantArrivedResponse.builder()
                .user(user)
                .build();
    }
}
