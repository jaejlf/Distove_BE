package distove.voice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Participant {

    private String id;
    private final Long userId;
    @JsonIgnore
    private final Room room;
    private final WebRtcEndpoint mediaEndpoint;
    private final WebSocketSession webSocketSession;
    private final List<IncomingParticipant> incomingParticipants = new ArrayList<>();

    public Participant(Long userId, Room room, WebSocketSession webSocketSession, WebRtcEndpoint mediaEndpoint) {
        this.userId = userId;
        this.room = room;
        this.webSocketSession = webSocketSession;
        this.mediaEndpoint = mediaEndpoint;
    }
}
