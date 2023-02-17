package distove.presence.repository;

import distove.presence.dto.Presence;
import distove.presence.dto.PresenceTime;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemoryUserConnectionRepository implements UserConnectionRepository{

    public static final Map<Long,String> userConnections = new HashMap<>();

    @Override
    public Boolean isUserConnected(Long userId){
        return userConnections.keySet().contains(userId);
    }
    @Override
    public void addUserConnection(Long userId,String sessionId){
        userConnections.put(userId,sessionId);
    }
    @Override
    public void removeUserConnection(String sessionId){
        userConnections.values().remove(sessionId);
    }
    @Override
    public Map<Long, String> findAll(){
        return userConnections;
    }
}
