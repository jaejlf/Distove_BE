package distove.voice.entity;

import lombok.Getter;
import org.kurento.client.WebRtcEndpoint;

@Getter
public class IncomingParticipant {

    private final Long userId;
    private final WebRtcEndpoint mediaEndpoint;

    public IncomingParticipant(Long userId, WebRtcEndpoint mediaEndpoint) {
        this.userId = userId;
        this.mediaEndpoint = mediaEndpoint;
    }
}
