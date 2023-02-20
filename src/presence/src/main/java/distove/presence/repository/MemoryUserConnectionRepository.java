package distove.presence.repository;

import distove.presence.dto.Presence;
import distove.presence.dto.PresenceTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
@Slf4j
@Repository
public class MemoryUserConnectionRepository implements UserConnectionRepository {

    public static final Map<Long, String> userConnections = new HashMap<>();

    @Override
    public Boolean isUserConnected(Long userId) {
        return userConnections.keySet().contains(userId);
    }

    @Override
    public void addUserConnection(Long userId, String sessionId) {
        userConnections.put(userId, sessionId);
    }

    @Override
    public void removeUserConnection(String sessionId) {
        userConnections.values().remove(sessionId);
    }

    @Override
    public Long findUserIdBySessionId(String sessionId) {
        Long userId = null;
        for (Long uid : userConnections.keySet()) {
            log.info("userId,sessionId {} {}", uid,sessionId);
            if(userConnections.get(uid).equals(sessionId)){
                userId = uid;
            }
        }
        return userId;
    }

    @Override
    public void removeUserConnectionByUserId(Long userId) {
        userConnections.remove(userId);
    }

    @Override
    public Map<Long, String> findAll() {
        return userConnections;
    }
}
