package distove.voice.repository;

import distove.voice.entity.Participant;
import distove.voice.entity.Room;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository

public class MemoryParticipantRepository implements ParticipantRepository {

    private static final List<Participant> participants = new ArrayList<>();

    @Override
    public Optional<Participant> findParticipantByWebSocketSession(WebSocketSession webSocketSession) {
        return participants.stream()
                .filter(participant -> participant.getWebSocketSession().equals(webSocketSession)).findAny();
    }

    @Override
    public Optional<Participant> findParticipantByUserId(Long userId) {
        return participants.stream()
                .filter(participant -> participant.getUserId().equals(userId)).findAny();
    }

    @Override
    public void deleteParticipant(Participant participant) {
        participants.remove(participant);

    }

    @Override
    public List<Participant> findParticipantsByRoom(Room room) {
        return participants.stream()
                .filter(participant -> participant.getRoom().equals(room)).collect(Collectors.toList());
    }

    @Override
    public void insert(Participant participant) {
        participants.add(participant);
    }

    @Override
    public List<Participant> findAll() {
        return participants;
    }
}
