package distove.voice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public class Participant {

    private int id;
    private final Long userId;
    @JsonIgnore
    private final Room room;
    private final WebRtcEndpoint mediaEndpoint;
    private final WebSocketSession webSocketSession;
    private final ConcurrentMap<Long, IncomingParticipant> incomingParticipants = new ConcurrentHashMap<>();

    public Participant(Long userId, Room room, WebRtcEndpoint mediaEndpoint, WebSocketSession webSocketSession) {
        this.userId = userId;
        this.room = room;
        this.mediaEndpoint = mediaEndpoint;
        this.webSocketSession = webSocketSession;

    }


}
