package distove.presence.repository;

import distove.presence.entity.Presence;

import java.util.Map;
import java.util.Optional;

public interface PresenceRepository {

    void save(Long userId, Presence presence);

    Optional<Presence> findByUserId(Long userId);

    Map<Long, Presence> findAll();

    void deleteByUserId(Long userId);

    Boolean isAway(Long userId);

}
