package distove.voice.dto.request;

import lombok.Getter;
import org.kurento.client.IceCandidate;

@Getter
public class SendIceCandidateRequest {
    private Long userId;
    private IceCandidate iceCandidate;
}
