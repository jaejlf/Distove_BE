package distove.voice.service;

import distove.voice.entity.Participant;
import distove.voice.entity.VoiceRoom;
import distove.voice.exception.DistoveException;
import distove.voice.repository.VoiceRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static distove.voice.exception.ErrorCode.ROOM_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoiceRoomService {

    private final KurentoClient kurentoClient;
    private final VoiceRoomRepository voiceRoomRepository;

    public VoiceRoom getByChannelId(Long channelId) {
        return findByChannelId(channelId)
                .orElseGet(() -> save(new VoiceRoom(channelId, kurentoClient.createMediaPipeline())));
    }

    public void close(VoiceRoom voiceRoom) {
        voiceRoom.getPipeline().release();
        deleteByChannelId(voiceRoom.getChannelId());
    }

    public Optional<VoiceRoom> findByChannelId(Long channelId) {
        return voiceRoomRepository.findByChannelId(channelId);
    }

    public VoiceRoom save(VoiceRoom voiceRoom) {
        return voiceRoomRepository.save(voiceRoom);
    }

    public VoiceRoom findByParticipant(Participant me) {
        return voiceRoomRepository.findByChannelId(me.getVoiceRoom().getChannelId())
                .orElseThrow(() -> new DistoveException(ROOM_NOT_FOUND_ERROR));
    }

    public List<VoiceRoom> findAll() {
        return voiceRoomRepository.findAll();
    }

    public void deleteByChannelId(Long channelId) {
        voiceRoomRepository.deleteByChannelId(channelId);
    }

    public void deleteAll() {
        voiceRoomRepository.deleteAll();
    }

}
