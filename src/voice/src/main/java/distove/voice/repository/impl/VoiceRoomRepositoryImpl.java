package distove.voice.repository.impl;

import distove.voice.entity.VoiceRoom;
import distove.voice.repository.VoiceRoomRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class VoiceRoomRepositoryImpl implements VoiceRoomRepository {

    public static final ConcurrentMap<Long, VoiceRoom> voiceRooms = new ConcurrentHashMap<>();

    @Override
    public VoiceRoom save(VoiceRoom voiceRoom) {
        voiceRooms.put(voiceRoom.getChannelId(), voiceRoom);
        return voiceRoom;
    }

    @Override
    public Optional<VoiceRoom> findByChannelId(Long channelId) {
        return Optional.ofNullable(voiceRooms.get(channelId));
    }

    @Override
    public List<VoiceRoom> findAll() {
        return new ArrayList<>(voiceRooms.values());
    }

    @Override
    public void deleteByChannelId(Long channelId) {
        voiceRooms.remove(channelId);
    }

    @Override
    public void deleteAll() {
        voiceRooms.clear();
    }

}
