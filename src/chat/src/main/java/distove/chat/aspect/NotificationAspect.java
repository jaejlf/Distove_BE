package distove.chat.aspect;

import distove.chat.service.ConnectionService;
import distove.chat.service.NotificationService;
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

import static java.util.Objects.requireNonNull;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationService notificationService;
    private final ConnectionService connectionService;

    /**
     * when : 새로운 메시지 발행 시
     * then : notifyNewMessage -> 새로운 메시지 발행 알림
     */
    @Pointcut("execution(* distove.chat.service.impl.CreateMessageGenerator.createMessage())")
    public void createMessageAspect() {
    }

    @After("createMessageAspect() && args(channelId)")
    public void notifyNewMessage(Long channelId) {
        notificationService.notifyNewMessage(channelId);
    }

    /**
     * when : 서버 구독 이벤트 발생 시
     * then : notifyUnreadOfChannels -> 모든 채널의 알림 정보 업데이트
     */
    @Before("execution(public * org.springframework.messaging.support.ChannelInterceptor.preSend(..))")
    public void notifyUnreadsOfChannelAspect(JoinPoint joinPoint) throws Throwable {
        Message<?> message = (Message<?>) joinPoint.getArgs()[0];
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            if (accessor.containsNativeHeader("userId")) {
                Long userId = Long.parseLong(requireNonNull(accessor.getNativeHeader("userId")).get(0));
                Long serverId = Long.parseLong(requireNonNull(accessor.getDestination()).split("/")[3]);
                notificationService.notifyUnreadOfChannels(userId, serverId);
            }
        }
    }

    /**
     * when : 특정 채널의 메시지 리스트 최초 조회 시 (scroll = DEFAULT)
     * then : notifyUnreadOfChannels -> 모든 채널의 알림 정보 업데이트
     */
    @Pointcut("execution(* distove.chat.controller.MessageController.getMessagesByChannelId())")
    public void getMessagesByChannelIdAspect() {
    }

    @Before("getMessagesByChannelIdAspect() && args(userId, channelId, scroll,..)")
    public void notifyUnreadOfChannels(Long userId, Long channelId, Integer scroll) {
        Long serverId = connectionService.getConnection(channelId).getServerId();
        if (scroll == null) notificationService.notifyUnreadOfChannels(userId, serverId);
    }

}
