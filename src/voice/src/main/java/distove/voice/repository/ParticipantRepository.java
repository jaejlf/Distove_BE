package distove.voice.repository;

import distove.voice.entity.Participant;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;


@Repository
public interface ParticipantRepository {
    Optional<Participant> findParticipantByWebSocketSession(WebSocketSession webSocketSession);

    Optional<Participant> findParticipantByUserId(Long userId);

    void deleteParticipant(Participant participant);

    List<Participant> findParticipantsByChannelId(Long channelId);

    void insert(Participant participant);

    void save(Long userId, Participant participant);

    List<Participant> findAll();

    void deleteAllParticipant();
}
