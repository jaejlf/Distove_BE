package distove.chat.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestController {

    private final EventQ eventQ;

    @GetMapping("/queue/{channelId}")
    public void clearAll(@PathVariable Long channelId) {
        Event event = new Event("토픽", channelId);
        eventQ.addQ(event);
        log.info(">>>>> " + event.getTopic() + " / " + event.getChannelId());
    }

}
