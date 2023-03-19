package distove.voice.repository;

import distove.voice.entity.Participant;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;


@Repository
public interface ParticipantRepository {

    Optional<Participant> findByWebSocketSession(WebSocketSession webSocketSession);

    Optional<Participant> findByUserId(Long userId);

    void add(Participant participant);

    void save(Long userId, Participant participant);

    List<Participant> findAll();

    List<Participant> findAllByChannelId(Long channelId);

    void delete(Participant participant);

    void deleteAll();

}
