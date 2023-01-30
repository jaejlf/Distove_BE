package distove.voice.repository;

import distove.voice.entity.Participant;
import distove.voice.entity.Room;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;


@Repository
public interface ParticipantRepository {
    Optional<Participant> findParticipantByWebSocketSession(WebSocketSession webSocketSession);

    Optional<Participant> findParticipantByUserId(Long userId);

    void deleteParticipant(Participant participant);

    List<Participant> findParticipantsByRoom(Room room);

    void insert(Participant participant);

    List<Participant> findAll();
}
