package distove.voice.repository;

import distove.voice.entity.VoiceRoom;

import java.util.List;
import java.util.Optional;

public interface VoiceRoomRepository {

    VoiceRoom save(VoiceRoom voiceRoom);

    Optional<VoiceRoom> findByChannelId(Long channelId);

    List<VoiceRoom> findAll();

    void deleteByChannelId(Long channelId);

    void deleteAll();

}
