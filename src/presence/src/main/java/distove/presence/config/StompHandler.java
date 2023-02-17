package distove.presence.config;

import distove.presence.dto.PresenceTime;
import distove.presence.enumerate.PresenceType;
import distove.presence.repository.PresenceRepository;
import distove.presence.repository.UserConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final UserConnectionRepository userConnectionRepository;
    private final PresenceRepository presenceRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Long userId = Long.valueOf(accessor.getNativeHeader("userId").get(0).toString());
            String sessionId = accessor.getSessionId();
            log.info("userId, sessionId : {} {}",userId,sessionId);
            userConnectionRepository.addUserConnection(userId,sessionId);
            presenceRepository.save(userId, PresenceTime.newPresenceTime(PresenceType.ONLINE.getPresence()));
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            userConnectionRepository.removeUserConnection(sessionId);

        }
        return message;
    }
}