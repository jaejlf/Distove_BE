package distove.presence.handler;

import distove.presence.entity.Presence;
import distove.presence.enumerate.ServiceInfo;
import distove.presence.event.UpdatePresenceEvent;
import distove.presence.repository.ConnectionRepository;
import distove.presence.repository.PresenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static distove.presence.enumerate.PresenceType.ONLINE;
import static distove.presence.event.EventTopic.getEventQ;
import static org.springframework.messaging.simp.stomp.StompCommand.DISCONNECT;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final ConnectionRepository connectionRepository;
    private final PresenceRepository presenceRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Long userId = Long.valueOf(accessor.getNativeHeader("userId").get(0).toString());
            connectionRepository.save(userId, sessionId);
            presenceRepository.save(userId, new Presence(ONLINE));
            getEventQ(UpdatePresenceEvent.class).add(UpdatePresenceEvent.of(userId, ServiceInfo.CONNECT.getType()));
        } else if (DISCONNECT.equals(accessor.getCommand())) {
            connectionRepository.findBySessionId(sessionId).ifPresent(userId -> {
                connectionRepository.deleteByUserId(userId);
                getEventQ(UpdatePresenceEvent.class).add(UpdatePresenceEvent.of(userId, ServiceInfo.DISCONNECT.getType()));
            });
        }
        return message;
    }

}
