package distove.voice.repository.impl;

import distove.voice.entity.Participant;
import distove.voice.repository.ParticipantRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository
public class ParticipantRepositoryImpl implements ParticipantRepository {

    public static final ConcurrentMap<Long, Participant> participants = new ConcurrentHashMap<>();

    @Override
    public Optional<Participant> findByWebSocketSession(WebSocketSession webSocketSession) {
        return participants.values().stream()
                .filter(participant -> participant.getWebSocketSession().equals(webSocketSession)).findAny();
    }

    @Override
    public Optional<Participant> findByUserId(Long userId) {
        return Optional.ofNullable(participants.get(userId));
    }

    @Override
    public void add(Participant participant) {
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
    public List<Participant> findAllByChannelId(Long channelId) {
        return participants.values().stream()
                .filter(participant -> participant.getVoiceRoom().getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Participant participant) {
        participants.remove(participant.getUserId());
    }

    @Override
    public void deleteAll() {
        participants.clear();
    }

}
