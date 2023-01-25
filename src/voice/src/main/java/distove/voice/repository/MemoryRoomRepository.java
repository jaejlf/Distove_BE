package distove.voice.repository;

import distove.voice.entity.Room;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository

public class MemoryRoomRepository implements RoomRepository {
    private static final List<Room> rooms = new ArrayList<>();

    @Override
    public Optional<Room> findRoomByChannelId(Long channelId) {
        return rooms.stream()
                .filter(room -> room.getChannelId().equals(channelId)).findAny();
    }

    @Override
    public Optional<Room> findRoomById(String id) {
        return rooms.stream()
                .filter(room -> room.getId().equals(id)).findAny();
    }

    @Override
    public Room save(Room room) {
        rooms.add(room);
        return room;
    }

    @Override
    public void deleteById(String id) {
        rooms.removeIf(room -> room.getId().equals(id));
    }

    @Override
    public List<Room> findAll() {
        return rooms;
    }
}
