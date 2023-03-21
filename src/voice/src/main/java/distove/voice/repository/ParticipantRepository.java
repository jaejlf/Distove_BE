package distove.voice.repository;

import distove.voice.entity.Participant;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository {

    Optional<Participant> findByWebSocketSession(WebSocketSession webSocketSession);

    Optional<Participant> findByUserId(Long userId);

    void save(Participant participant);

    List<Participant> findAll();

    List<Participant> findAllByChannelId(Long channelId);

    void delete(Participant participant);

    void deleteAll();

}
