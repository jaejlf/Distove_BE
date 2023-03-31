package distove.presence.controller;

import distove.presence.event.UpdatePresenceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static distove.presence.event.EventTopic.getEventQ;

@RestController
@RequiredArgsConstructor
public class WebController {

    @PostMapping("/web/update")
    void updatePresence(@RequestHeader Long userId,
                        @RequestParam String type) {
        getEventQ(UpdatePresenceEvent.class).add(new UpdatePresenceEvent(userId, type));
    }

}
