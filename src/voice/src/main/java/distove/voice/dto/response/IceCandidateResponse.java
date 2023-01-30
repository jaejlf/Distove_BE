package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.enumerate.MessageType;
import lombok.Builder;
import org.kurento.client.IceCandidate;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Builder

public class IceCandidateResponse {
    private final String type = MessageType.ICE_CANDIDATE.getType();
    private final Long userId;
    private final IceCandidate candidateInfo;

    public static IceCandidateResponse newIceCandidateResponse(Long userId, IceCandidate candidateInfo) {
        return IceCandidateResponse.builder()
                .userId(userId)
                .candidateInfo(candidateInfo)
                .build();
    }
}
