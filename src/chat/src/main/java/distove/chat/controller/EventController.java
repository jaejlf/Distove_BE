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

    @PostMapping("/web/connection/server/{serverId}")
    public void createConnection(@PathVariable Long serverId,
                                 @RequestParam Long channelId) {
        connectionService.createConnection(serverId, channelId);
    }

    @DeleteMapping("/web/clear/{channelId}")
    public void clear(@PathVariable Long channelId) {
        eventService.requestDelChannel(channelId);
    }

    @DeleteMapping("/web/clear/list")
    public void clearAll(@RequestParam List<Long> channelIds) {
        eventService.requestDelChannelList(channelIds);
    }

}