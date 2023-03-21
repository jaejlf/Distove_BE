package distove.presence.repository.impl;

import distove.presence.entity.Presence;
import distove.presence.repository.PresenceRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class PresenceRepositoryImpl implements PresenceRepository {

    public static final Map<Long, Presence> presences = new LinkedHashMap<>();

    @Override
    public void save(Long userId, Presence presence) {
        presences.put(userId, presence);
    }

    @Override
    public Optional<Presence> findByUserId(Long userId) {
        return Optional.ofNullable(presences.get(userId));
    }

    @Override
    public Map<Long, Presence> findAll() {
        return presences;
    }

    @Override
    public void deleteByUserId(Long userId) {
        presences.remove(userId);
    }

    @Override
    public Boolean isAway(Long userId) {
        return !presences.containsKey(userId);
    }

}
