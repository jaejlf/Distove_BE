package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import org.kurento.client.IceCandidate;

import static distove.voice.enumerate.MessageType.ICE_CANDIDATE;

@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IceCandidateResponse {

    private final String message = ICE_CANDIDATE.getMessage();
    private final Long userId;
    private final IceCandidate iceCandidate;

    public static IceCandidateResponse of(Long userId, IceCandidate iceCandidate) {
        return IceCandidateResponse.builder()
                .userId(userId)
                .iceCandidate(iceCandidate)
                .build();
    }

}
