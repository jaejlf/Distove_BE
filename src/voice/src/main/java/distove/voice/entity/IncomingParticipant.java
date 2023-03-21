package distove.voice.entity;

import lombok.Getter;
import org.kurento.client.WebRtcEndpoint;

@Getter
public class IncomingParticipant {

    private final Long userId;
    private final WebRtcEndpoint endpoint;

    public IncomingParticipant(Long userId, WebRtcEndpoint endpoint) {
        this.userId = userId;
        this.endpoint = endpoint;
    }

}
