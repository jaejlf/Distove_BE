package distove.voice.repository;

import distove.voice.entity.Room;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository {
    Optional<Room> findRoomByChannelId(Long channelId);

    Optional<Room> findRoomById(String id);

    Room save(Room room);

    void deleteById(String id);

    List<Room> findAll();
}
