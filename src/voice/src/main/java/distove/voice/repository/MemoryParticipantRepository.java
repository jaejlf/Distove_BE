package distove.voice.repository;

import distove.voice.entity.Participant;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository

public class MemoryParticipantRepository implements ParticipantRepository {

    //    private static final List<Participant> participants = new CopyOnWriteArrayList<>();
    public static final ConcurrentMap<Long, Participant> participants = new ConcurrentHashMap<>();

    @Override
    public Optional<Participant> findParticipantByWebSocketSession(WebSocketSession webSocketSession) {
        return participants.values().stream()
                .filter(participant -> participant.getWebSocketSession().equals(webSocketSession)).findAny();
    }

    @Override
    public Optional<Participant> findParticipantByUserId(Long userId) {
        return Optional.ofNullable(participants.get(userId));
    }

    @Override
    public void deleteParticipant(Participant participant) {
        participants.remove(participant.getUserId());

    }

    @Override
    public List<Participant> findParticipantsByChannelId(Long channelId) {
        return participants.values().stream()
                .filter(participant -> participant.getRoom().getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public void insert(Participant participant) {
        participants.put(participant.getUserId(), participant);
    }

    @Override
    public void save(Long userId, Participant participant) {
        participants.put(participant.getUserId(), participant);
    }

    @Override
    public List<Participant> findAll() {
        return new ArrayList<>(participants.values());


    }
    @Override
    public void deleteAllParticipant(){
        participants.clear();
    }
}
