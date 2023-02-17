package distove.presence.repository;

import distove.presence.dto.Presence;
import distove.presence.dto.PresenceTime;

import java.util.Map;
import java.util.Optional;

public interface PresenceRepository {

    Optional<PresenceTime> findPresenceByUserId(Long userId);
    void removePresenceByUserId(Long userId);
    Map<Long, PresenceTime> findAll();
    Boolean isUserOnline(Long userId);
    Presence save(Long userId,PresenceTime presenceTime);
}
