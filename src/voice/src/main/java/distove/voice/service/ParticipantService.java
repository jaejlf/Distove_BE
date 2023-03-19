package distove.voice.service;

import distove.voice.entity.Participant;
import distove.voice.exception.DistoveException;
import distove.voice.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static distove.voice.exception.ErrorCode.PARTICIPANT_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public Participant findByWebSocketSession(WebSocketSession webSocketSession) {
        return participantRepository.findByWebSocketSession(webSocketSession)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
    }

    public Participant findByUserId(Long senderUserId) {
        return participantRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
    }

    public void add(Participant me) {
        participantRepository.add(me);
    }

    public void save(Participant me) {
        participantRepository.save(me.getUserId(), me);
    }

    public List<Participant> findAll() {
        return participantRepository.findAll();
    }

    public List<Participant> findAllByChannelId(Long channelId) {
        return participantRepository.findAllByChannelId(channelId);
    }

    public void delete(Participant me) {
        participantRepository.delete(me);
    }

    public void deleteAll() {
        participantRepository.deleteAll();
    }

}
