package distove.chat.aspect;

import distove.chat.client.PresenceClient;
import distove.chat.enumerate.PresenceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PresenceAspect {

    private final PresenceClient presenceClient;

    @Before("execution(public * org.springframework.messaging.support.ChannelInterceptor.preSend(..))")
    public void updateUserPresence(JoinPoint joinPoint) throws Throwable {
        Message<?> message = (Message<?>) joinPoint.getArgs()[0];
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            Long userId = Long.valueOf(accessor.getNativeHeader("userId").get(0).toString());
            presenceClient.updateUserPresence(userId, PresenceType.ONLINE.getType());
        }
    }

}
