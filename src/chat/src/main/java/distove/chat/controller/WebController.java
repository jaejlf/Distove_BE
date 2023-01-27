package distove.chat.controller;

import distove.chat.service.ConnectionService;
import distove.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class WebController {

    private final ConnectionService connectionService;
    private final MessageService messageService;

    @PostMapping("/connection/{channelId}")
    public void createConnection(@RequestHeader("userId") Long userId,
                                 @PathVariable Long channelId) {
        connectionService.createConnection(channelId, userId);
    }

    @DeleteMapping("/clear/{channelId}")
    public void clearAll(@PathVariable Long channelId) {
        connectionService.clearAll(channelId);
        messageService.clearAll(channelId);
    }

}