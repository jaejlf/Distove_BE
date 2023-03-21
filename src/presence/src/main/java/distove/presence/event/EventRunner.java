package distove.presence.event;

import distove.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventRunner {

    private final PresenceService presenceService;

    public void runUpdateUserPresence(UpdateUserPresenceEvent updateUserPresenceEvent){
        presenceService.updateUserPresence(updateUserPresenceEvent.getUserId(),updateUserPresenceEvent.getServiceInfo());
    }

    public void runSendNewUserConnection(SendNewUserConnectionEvent sendNewUserConnectionEvent){
        presenceService.sendNewUserConnectionEvent(sendNewUserConnectionEvent.getUserId(),sendNewUserConnectionEvent.getPresenceType());
    }

}
