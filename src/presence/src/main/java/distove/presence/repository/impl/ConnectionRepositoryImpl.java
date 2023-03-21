package distove.presence.repository.impl;

import distove.presence.repository.ConnectionRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ConnectionRepositoryImpl implements ConnectionRepository {

    public static final Map<Long, String> connections = new LinkedHashMap<>();

    @Override
    public void save(Long userId, String sessionId) {
        connections.put(userId, sessionId);
    }

    @Override
    public Boolean isConnected(Long userId) {
        return connections.containsKey(userId);
    }

    @Override
    public Optional<Long> findBySessionId(String sessionId) {
        for (Long userId : connections.keySet()) {
            if (connections.get(userId).equals(sessionId)) return Optional.ofNullable(userId);
        }
        return Optional.empty();
    }

    @Override
    public void deleteByUserId(Long userId) {
        connections.remove(userId);
    }

}
