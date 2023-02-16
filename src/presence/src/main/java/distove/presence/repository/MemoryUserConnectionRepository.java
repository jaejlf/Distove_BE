package distove.presence.repository;

import distove.presence.dto.Presence;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemoryUserConnectionRepository implements UserConnectionRepository{

    public static final Set<Long> userConnections = new HashSet<>();

    @Override
    public Boolean isUserConnected(Long userId){
        return userConnections.contains(userId);
    }
    @Override
    public void addUserConnection(Long userId){
        userConnections.add(userId);
    }
    @Override
    public void removeUserConnection(Long userId){
        userConnections.remove(userId);
    }
}
