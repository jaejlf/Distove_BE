package distove.presence.service;

import distove.presence.event.UpdateUserPresenceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static distove.presence.enumerate.EventTopic.getEventQ;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final PresenceService presenceService;

    public void requestUpdateUserPresence(Long userId){
        getEventQ(UpdateUserPresenceEvent.class).add(UpdateUserPresenceEvent.of(userId));
    }

    public void runUpdateUserPresenceEvent(UpdateUserPresenceEvent updateUserPresenceEvent){
        presenceService.updateUserPresence(updateUserPresenceEvent.getUserId());
    }

}
