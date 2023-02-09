package distove.voice.dto.request;

import lombok.Getter;
import org.kurento.client.IceCandidate;


@Getter
public class AddIceCandidateRequest {
    private String type;
    private Long userId;
    private IceCandidate candidateInfo;
}
