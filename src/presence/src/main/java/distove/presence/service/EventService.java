package distove.presence.service;

import distove.presence.enumerate.PresenceType;
import distove.presence.event.SendNewUserConnectionEvent;
import distove.presence.event.UpdateUserPresenceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class EventService {

    private final PresenceService presenceService;


    public void runUpdateUserPresence(UpdateUserPresenceEvent updateUserPresenceEvent){
        presenceService.updateUserPresence(updateUserPresenceEvent.getUserId(),updateUserPresenceEvent.getServiceInfo());
    }

    public void runSendNewUserConnection(SendNewUserConnectionEvent sendNewUserConnectionEvent){
        presenceService.sendNewUserConnectionEvent(sendNewUserConnectionEvent.getUserId(),sendNewUserConnectionEvent.getPresenceType());
    }

}
