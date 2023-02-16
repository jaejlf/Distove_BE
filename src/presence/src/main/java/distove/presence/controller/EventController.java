package distove.presence.controller;


import distove.presence.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RequiredArgsConstructor
@Controller
public class EventController {
    private final EventService eventService;

    @PostMapping("/update")
    void updateUserPresence(@RequestHeader("userId") Long userId){
        eventService.requestUpdateUserPresence(userId);
    }


}
