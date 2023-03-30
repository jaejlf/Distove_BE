package distove.voice.aspect;

import distove.voice.client.PresenceClient;
import distove.voice.entity.Participant;
import distove.voice.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import static distove.voice.enumerate.PresenceType.VOICE_OFF;
import static distove.voice.enumerate.PresenceType.VOICE_ON;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PresenceAspect {

    private final PresenceClient presenceClient;
    private final ParticipantService participantService;

    @Pointcut("execution(* distove.voice.handler.SignalingHandler.join())")
    public void joinRoomAspect() {
    }

    @Pointcut("execution(* distove.voice.handler.SignalingHandler.leave())")
    public void leaveRoomAspect() {
    }

    @Before("joinRoomAspect() && args(session,..)")
    public void updatePresenceToVoiceOn(WebSocketSession session) {
        Participant participant = participantService.getByWebSocketSession(session);
        presenceClient.updateUserPresence(participant.getUserId(), VOICE_ON.getType());
    }

    @Before("leaveRoomAspect() && args(session,..)")
    public void updatePresenceToVoiceOff(WebSocketSession session) {
        Participant participant = participantService.getByWebSocketSession(session);
        presenceClient.updateUserPresence(participant.getUserId(), VOICE_OFF.getType());
    }

}
