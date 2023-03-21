package distove.presence.repository;

public interface UserConnectionRepository {

    Boolean isUserConnected(Long userId);

    void addUserConnection(Long userId, String sessionId);

    Long findUserIdBySessionId(String sessionId);

    void removeUserConnectionByUserId(Long userId);

}
