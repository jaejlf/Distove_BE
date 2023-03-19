package distove.chat.client;

import distove.chat.event.DeleteChannelEvent;
import distove.chat.event.DeleteChannelsEvent;
import distove.chat.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static distove.chat.enumerate.EventTopic.getEventQ;

@RestController
@RequestMapping("/web")
@RequiredArgsConstructor
public class WebController {

    private final ConnectionService connectionService;

    @PostMapping("/connection/server/{serverId}")
    public void createConnection(@PathVariable Long serverId,
                                 @RequestParam Long channelId) {
        connectionService.createConnection(serverId, channelId);
    }

    @DeleteMapping("/clear/{channelId}")
    public void clear(@PathVariable Long channelId) {
        getEventQ(DeleteChannelEvent.class)
                .add(new DeleteChannelEvent(channelId));
    }

    @DeleteMapping("/clear/list")
    public void clearAll(@RequestParam List<Long> channelIds) {
        getEventQ(DeleteChannelsEvent.class)
                .add(new DeleteChannelsEvent(channelIds));
    }

}
