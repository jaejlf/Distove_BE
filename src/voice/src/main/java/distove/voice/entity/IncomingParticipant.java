package distove.voice.entity;

import lombok.Builder;
import lombok.Getter;
import org.kurento.client.WebRtcEndpoint;

@Getter
@Builder
public class IncomingParticipant {

    private final Long userId;
    private final WebRtcEndpoint mediaEndpoint;

    public static IncomingParticipant newIncomingParticipant(Long userId, WebRtcEndpoint mediaEndpoint) {
        return IncomingParticipant.builder()
                .userId(userId)
                .mediaEndpoint(mediaEndpoint)
                .build();
    }
}
