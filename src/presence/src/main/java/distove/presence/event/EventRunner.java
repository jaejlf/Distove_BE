package distove.presence.event;

import distove.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventRunner {

    private final PresenceService presenceService;

    public void runUpdatePresence(UpdatePresenceEvent updatePresenceEvent){
        presenceService.updatePresence(updatePresenceEvent.getUserId(), updatePresenceEvent.getType());
    }

}
