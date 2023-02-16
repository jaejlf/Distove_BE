package distove.presence.repository;

import distove.presence.dto.Presence;
import distove.presence.dto.PresenceTime;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryPresenceRepository implements PresenceRepository{
    public static final Map<Long, PresenceTime> presences = new LinkedHashMap<>();

    @Override
    public Optional<Presence> findPresenceByUserId(Long userId) {
        return Optional.ofNullable(presences.get(userId).getPresence());
    }

    @Override
    public void removePresenceByUserId(Long userId){
        presences.remove(userId);
    }

    @Override
    public Map<Long,PresenceTime> findAll(){
        return presences;
    }

    @Override
    public Boolean isUserOnline(Long userId){
        return presences.containsKey(userId);
    }

    @Override
    public Presence save(Long userId,PresenceTime presenceTime){
        return presences.put(userId,presenceTime).getPresence();
    }
}
