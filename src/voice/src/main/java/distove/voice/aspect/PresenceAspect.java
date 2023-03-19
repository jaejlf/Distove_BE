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

    @Pointcut("execution(* distove.voice.service.SignalingService.joinRoom())")
    public void joinRoomAspect() {
    }

    @Pointcut("execution(* distove.voice.service.SignalingService.leaveRoom())")
    public void leaveRoomAspect() {
    }

    @Before("joinRoomAspect() && args(userId,..)")
    public void updatePresenceToVoiceOn(Long userId) {
        log.info(">>>>> Presence 업데이트 -> VOICE ON");

        presenceClient.updateUserPresence(userId, VOICE_ON.getType());
    }

    @Before("leaveRoomAspect() && args(webSocketSession,..)")
    public void updatePresenceToVoiceOff(WebSocketSession webSocketSession) {
        log.info(">>>>> Presence 업데이트 -> VOICE OFF");

        Participant participant = participantService.findByWebSocketSession(webSocketSession);
        presenceClient.updateUserPresence(participant.getUserId(), VOICE_OFF.getType());
    }

}
