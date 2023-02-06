package distove.voice.dto.response;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.enumerate.MessageType;
import lombok.Builder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder
public class LeftRoomResponse {
    private final String type = MessageType.PARTICIPANT_LEFT.getType();
    private final Long userId;


    public static LeftRoomResponse newLeftRoomResponse(Long userId) {
        return LeftRoomResponse.builder()
                .userId(userId)
                .build();
    }
}
