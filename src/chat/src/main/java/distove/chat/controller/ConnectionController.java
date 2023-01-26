package distove.chat.controller;

import distove.chat.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping("/connection/{channelId}")
    public void publishFile(@RequestHeader("userId") Long userId,
                            @PathVariable Long channelId) {
        connectionService.createConnection(channelId, userId);
    }

}