package distove.presence.repository;

import distove.presence.entity.PresenceTime;

import java.util.Map;
import java.util.Optional;

public interface PresenceRepository {
    Optional<PresenceTime> findPresenceByUserId(Long userId);
    void removePresenceByUserId(Long userId);
    Map<Long, PresenceTime> findAll();
    Boolean isUserOnline(Long userId);
    void save(Long userId, PresenceTime presenceTime);
    void removePresenceByUserIdIfOffline(Long userId);
}
