package distove.presence.service;

import distove.presence.enumerate.PresenceType;
import distove.presence.event.SendNewUserConnectionEvent;
import distove.presence.event.UpdateUserPresenceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static distove.presence.enumerate.EventTopic.getEventQ;

@Service
@Slf4j
public class RequestEventService {

    public void requestUpdateUserPresence(Long userId,String serviceInfo){
        getEventQ(UpdateUserPresenceEvent.class).add(UpdateUserPresenceEvent.of(userId,serviceInfo));
    }
    public void requestSendNewUserConnection(Long userId, PresenceType presenceType){
        getEventQ(SendNewUserConnectionEvent.class).add(SendNewUserConnectionEvent.of(userId,presenceType));
    }
}
