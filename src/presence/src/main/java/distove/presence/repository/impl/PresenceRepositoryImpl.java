package distove.presence.repository.impl;

import distove.presence.entity.PresenceTime;
import distove.presence.repository.PresenceRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class PresenceRepositoryImpl implements PresenceRepository {
    public static final Map<Long, PresenceTime> presences = new LinkedHashMap<>();

    @Override
    public Optional<PresenceTime> findPresenceByUserId(Long userId) {
        return Optional.ofNullable(presences.get(userId));
    }

    @Override
    public void removePresenceByUserId(Long userId) {
        presences.remove(userId);
    }

    @Override
    public void removePresenceByUserIdIfOffline(Long userId) {
        presences.remove(userId);
    }

    @Override
    public Map<Long, PresenceTime> findAll() {
        return presences;
    }

    @Override
    public Boolean isUserOnline(Long userId) {
        return presences.containsKey(userId);
    }

    @Override
    public void save(Long userId, PresenceTime presenceTime) {
        presences.put(userId, presenceTime);
    }

}
