package distove.voice.repository;

import distove.voice.entity.Room;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository {
    Optional<Room> findRoomByChannelId(Long channelId);

    Room save(Room room);

    void deleteByChannelId(Long channelId);

    List<Room> findAll();
    void deleteAllRoom();
}
