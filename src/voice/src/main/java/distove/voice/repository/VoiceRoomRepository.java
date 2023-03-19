package distove.voice.repository;

import distove.voice.entity.VoiceRoom;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoiceRoomRepository {

    Optional<VoiceRoom> findByChannelId(Long channelId);

    void deleteByChannelId(Long channelId);

    void deleteAll();

    VoiceRoom save(VoiceRoom voiceRoom);

    List<VoiceRoom> findAll();

}
