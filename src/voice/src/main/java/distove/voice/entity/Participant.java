package distove.voice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public class Participant implements Cloneable {

    private final Long userId;

    @JsonIgnore
    private final VoiceRoom voiceRoom;

    private final WebRtcEndpoint mediaEndpoint;
    private final WebSocketSession webSocketSession;
    private VideoSetting videoSetting;
    private final ConcurrentMap<Long, IncomingParticipant> incomingParticipants = new ConcurrentHashMap<>();

    public Participant(Long userId, VoiceRoom voiceRoom, WebRtcEndpoint mediaEndpoint, WebSocketSession webSocketSession, VideoSetting videoSetting) {
        this.userId = userId;
        this.voiceRoom = voiceRoom;
        this.mediaEndpoint = mediaEndpoint;
        this.webSocketSession = webSocketSession;
        this.videoSetting = videoSetting;
    }

    public void updateVideoInfo(VideoSetting videoSetting) {
        this.videoSetting = videoSetting;
    }

    @Override
    public Participant clone() {
        try {
            return (Participant) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
