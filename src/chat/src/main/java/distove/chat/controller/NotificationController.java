package distove.chat.controller;

import distove.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @Value("${sub.destination}")
    private String destination;

    @MessageMapping("/chat/server/{serverId}")
    public void publishNotification(@Header("userId") Long userId,
                                    @DestinationVariable Long serverId) {
        notificationService.publishAllNotification(userId, serverId);
    }


}
