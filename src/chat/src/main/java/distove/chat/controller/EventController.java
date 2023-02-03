package distove.chat.controller;

import distove.chat.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventService eventService;

    @PostMapping("/connection/{channelId}")
    public void createConnection(@RequestHeader("userId") Long userId,
                                 @PathVariable Long channelId) {
        eventService.requestNewChannel(userId, channelId);
    }

    @DeleteMapping("/clear/{channelId}")
    public void clearAll(@PathVariable Long channelId) {
        eventService.requestDelChannel(channelId);
    }

}