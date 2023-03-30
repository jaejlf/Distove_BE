package distove.chat.listener;

import distove.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static java.util.Objects.requireNonNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final NotificationService notificationService;

    /**
     * 서버 구독 이벤트 발생 시, Push All Notification!
     */
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String topic = headerAccessor.getDestination();

        if (headerAccessor.containsNativeHeader("userId")) {
            Long userId = Long.parseLong(requireNonNull(headerAccessor.getNativeHeader("userId")).get(0));
            Long serverId = Long.parseLong(requireNonNull(headerAccessor.getDestination()).split("/")[3]);
            notificationService.notifyUnreadOfChannels(userId, serverId);
        }
    }

}
