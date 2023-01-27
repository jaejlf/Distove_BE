package distove.voice.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import distove.voice.entity.Participant;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class ExistingParticipantsResponse {
    private final String type = "existingParticipants";
    private final List<Long> participantsUserIds;

    @Builder
    public ExistingParticipantsResponse(List<Participant> participants) {
        this.participantsUserIds = participants.stream().map(participant -> participant.getUserId())
                .collect(Collectors.toList());
    }
}
