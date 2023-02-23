package distove.chat.stomp;

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
public class StompSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final NotificationService notificationService;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.containsNativeHeader("userId")) {
            Long userId = Long.parseLong(requireNonNull(headerAccessor.getNativeHeader("userId")).get(0));
            Long serverId = Long.parseLong(requireNonNull(headerAccessor.getDestination()).split("/")[3]);

            log.info(">>>>> SUBSCRIBE EVENT! userId = " + userId + ", serverId = " + serverId);
            notificationService.publishAllNotification(userId, serverId); // '서버' 구독
        }
    }

}