package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.enumerate.MessageType;
import distove.voice.web.UserResponse;
import lombok.Builder;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder
public class ExistingParticipantsResponse {
    private final String type = MessageType.EXISTING_PARTICIPANTS.getType();
    private final List<UserVideoResponse> users;


    public static ExistingParticipantsResponse newExistingParticipantsResponse(List<UserVideoResponse> users) {
        return ExistingParticipantsResponse.builder()
                .users(users).build();
    }
}
