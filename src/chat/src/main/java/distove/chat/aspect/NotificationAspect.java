package distove.chat.aspect;

import distove.chat.event.NotifyNewMessageEvent;
import distove.chat.event.NotifyUnreadsEvent;
import distove.chat.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import static distove.chat.event.process.EventTopic.getEventQ;
import static java.util.Objects.requireNonNull;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationAspect {

//    private final NotificationService notificationService;
    private final ConnectionService connectionService;

    @Pointcut("execution(* distove.chat.service.impl.CreateMessageGenerator.createMessage())")
    public void createMessageAspect() {
    }

    @Pointcut("execution(* distove.chat.controller.MessageController.getMessagesByChannelId())")
    public void getMessagesByChannelIdAspect() {
    }

    /**
     * @when 새로운 메시지 발행 시
     * @then notifyNewMessage
     */
    @After("createMessageAspect() && args(channelId)")
    public void notifyNewMessage(Long channelId) {
        getEventQ(NotifyNewMessageEvent.class).add(new NotifyNewMessageEvent(channelId));
    }

    /**
     * @when 서버 구독 이벤트 발생 시
     * @then notifyUnreadOfChannels
     */
    @Before("execution(public * org.springframework.messaging.support.ChannelInterceptor.preSend(..))")
    public void notifyUnreadsOfChannelAspect(JoinPoint joinPoint) throws Throwable {
        Message<?> message = (Message<?>) joinPoint.getArgs()[0];
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            if (accessor.containsNativeHeader("userId")) {
                Long userId = Long.parseLong(requireNonNull(accessor.getNativeHeader("userId")).get(0));
                Long serverId = Long.parseLong(requireNonNull(accessor.getDestination()).split("/")[4]);
                getEventQ(NotifyUnreadsEvent.class).add(new NotifyUnreadsEvent(userId, serverId));
            }
        }
    }

    /**
     * @when 채널 이동 시 (= 특정 채널의 메시지 리스트 최초 조회 시)
     * @then notifyUnreadOfChannels
     */
    @Before("getMessagesByChannelIdAspect() && args(userId, channelId, scroll,..)")
    public void notifyUnreadOfChannels(Long userId, Long channelId, Integer scroll) {
        Long serverId = connectionService.getConnection(channelId).getServerId();
        if (scroll == null) getEventQ(NotifyUnreadsEvent.class).add(new NotifyUnreadsEvent(userId, serverId));
    }

}
