package distove.voice.dto.request;

import lombok.Getter;
import org.kurento.client.IceCandidate;


@Getter
public class AddIceCandidateRequest {
    String type;
    Long userId;
    IceCandidate candidateInfo;
}
