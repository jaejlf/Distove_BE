package distove.chat.controller;

import distove.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/chat/server/{serverId}")
    public void publishNotification(@Header("userId") Long userId,
                                    @DestinationVariable Long serverId) {
        notificationService.notifyUnreadOfChannels(userId, serverId);
    }

}
