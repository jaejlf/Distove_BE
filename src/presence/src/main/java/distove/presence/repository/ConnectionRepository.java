package distove.presence.repository;

import java.util.Optional;

public interface ConnectionRepository {

    void save(Long userId, String sessionId);

    Boolean isConnected(Long userId);

    Optional<Long> findBySessionId(String sessionId);

    void deleteByUserId(Long userId);

}
