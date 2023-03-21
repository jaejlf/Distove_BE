package distove.voice.service;

import distove.voice.entity.VoiceRoom;
import distove.voice.repository.VoiceRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoiceRoomService {

    private final KurentoClient kurentoClient;
    private final VoiceRoomRepository voiceRoomRepository;

    public Optional<VoiceRoom> findByChannelId(Long channelId) {
        return voiceRoomRepository.findByChannelId(channelId);
    }

    public VoiceRoom create(Long channelId) {
        VoiceRoom voiceRoom = new VoiceRoom(channelId, kurentoClient.createMediaPipeline());
        return voiceRoomRepository.save(voiceRoom);
    }

    public void delete(Long channelId) {
        voiceRoomRepository.deleteByChannelId(channelId);
    }

    public List<VoiceRoom> findAll() {
        return voiceRoomRepository.findAll();
    }

    public void deleteAll() {
        voiceRoomRepository.deleteAll();
    }

}
