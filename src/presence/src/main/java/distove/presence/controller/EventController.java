package distove.presence.controller;

import distove.presence.service.RequestEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class EventController {
    private final RequestEventService requestEventService;

    @PostMapping("/web/update")
    void updateUserPresence(@RequestHeader("userId") Long userId,
                            @RequestParam("serviceInfo") String serviceInfo){
        requestEventService.requestUpdateUserPresence(userId,serviceInfo);
    }


}
