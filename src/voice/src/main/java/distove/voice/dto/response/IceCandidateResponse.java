package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import org.kurento.client.IceCandidate;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class IceCandidateResponse {
    private final String type = "iceCandidate";
    private final Long userId;
    private final IceCandidate candidateInfo;

    @Builder

    public IceCandidateResponse(Long userId, IceCandidate candidateInfo) {
        this.userId = userId;
        this.candidateInfo = candidateInfo;
    }
}
