package distove.chat.listener;

import distove.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompUnSubscribeEventListener implements ApplicationListener<SessionUnsubscribeEvent> {

    private final MessageService messageService;

    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.containsNativeHeader("userId")) {
            Long userId = Long.parseLong(Objects.requireNonNull(headerAccessor.getNativeHeader("userId")).get(0));
            Long channelId = Long.parseLong(Objects.requireNonNull(headerAccessor.getDestination()).replace("/sub/", ""));
            messageService.unsubscribeChannel(userId, channelId); // '채널' 구독 해제
        }
    }

}
