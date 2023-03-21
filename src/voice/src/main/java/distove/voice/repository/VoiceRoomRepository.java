package distove.voice.repository;

import distove.voice.entity.VoiceRoom;

import java.util.List;
import java.util.Optional;

public interface VoiceRoomRepository {

    Optional<VoiceRoom> findByChannelId(Long channelId);

    void deleteByChannelId(Long channelId);

    void deleteAll();

    VoiceRoom save(VoiceRoom voiceRoom);

    List<VoiceRoom> findAll();

}
