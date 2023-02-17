package distove.voice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@Builder
public class Participant implements Cloneable {

    private final Long userId;
    @JsonIgnore
    private final Room room;
    private final WebRtcEndpoint mediaEndpoint;
    private final WebSocketSession webSocketSession;
    private VideoInfo videoInfo;
    private final ConcurrentMap<Long, IncomingParticipant> incomingParticipants = new ConcurrentHashMap<>();

    public static Participant of(Long userId, Room room, WebRtcEndpoint mediaEndpoint, WebSocketSession webSocketSession,VideoInfo videoInfo) {
        return Participant.builder()
                .userId(userId)
                .room(room)
                .mediaEndpoint(mediaEndpoint)
                .webSocketSession(webSocketSession)
                .videoInfo(videoInfo)
                .build();

    }
    public void updateVideoInfoOfParticipant (VideoInfo videoInfo){
        this.videoInfo = videoInfo;
    }


}
