package distove.voice.repository;

import distove.voice.entity.Room;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class MemoryRoomRepository implements RoomRepository {
    public static final ConcurrentMap<Long, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public Optional<Room> findRoomByChannelId(Long channelId) {
        return Optional.ofNullable(rooms.get(channelId));
    }

    @Override
    public Room save(Room room) {
        rooms.put(room.getChannelId(), room);
        return room;
    }

    @Override
    public void deleteByChannelId(Long channelId) {
        rooms.remove(channelId);
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    @Override
    public void deleteAllRoom(){
        rooms.clear();
    }
}
