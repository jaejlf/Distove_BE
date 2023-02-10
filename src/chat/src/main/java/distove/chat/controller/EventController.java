package distove.chat.controller;

import distove.chat.service.ConnectionService;
import distove.chat.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventService eventService;
    private final ConnectionService connectionService;

    @PostMapping("/connection/server/{serverId}")
    public void createConnection(@RequestHeader("userId") Long userId,
                                 @PathVariable Long serverId,
                                 @RequestParam Long channelId) {
        connectionService.createConnection(userId, serverId, channelId);
    }

    @DeleteMapping("/clear/{channelId}")
    public void clear(@PathVariable Long channelId) {
        eventService.requestDelChannel(channelId);
    }

    @DeleteMapping("/clear/list")
    public void clearAll(@RequestParam List<Long> channelIds) {
        eventService.requestDelChannelList(channelIds);
    }

}