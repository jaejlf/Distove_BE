package distove.presence.controller;

import distove.presence.event.UpdateUserPresenceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static distove.presence.enumerate.EventTopic.getEventQ;

@RestController
@RequiredArgsConstructor
public class WebController {

    @PostMapping("/web/update")
    void updateUserPresence(@RequestHeader Long userId,
                            @RequestParam String serviceInfo) {
        getEventQ(UpdateUserPresenceEvent.class).add(UpdateUserPresenceEvent.of(userId, serviceInfo));
    }

}
