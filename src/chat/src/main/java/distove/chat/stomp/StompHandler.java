package distove.chat.stomp;

import distove.chat.enumerate.ServiceInfoType;
import distove.chat.web.PresenceClient;
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

    private final PresenceClient presenceClient;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            Long userId = Long.valueOf(accessor.getNativeHeader("userId").get(0).toString());
            presenceClient.updateUserPresence(userId, ServiceInfoType.CHAT.getType());
        }
        return message;
    }

}