package distove.presence.repository;

import distove.presence.dto.Presence;
import distove.presence.dto.PresenceTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserConnectionRepository {

    Boolean isUserConnected(Long userId);
    void addUserConnection(Long userId, String sessionId);
    Long findUserIdBySessionId(String sessionId);
    void removeUserConnection(String sessionId);
    void removeUserConnectionByUserId(Long userId);
    Map<Long, String> findAll();


}
