package distove.voice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import distove.voice.dto.request.JoinRoomRequest;
import distove.voice.dto.request.SdpOfferRequest;
import distove.voice.dto.request.SendIceCandidateRequest;
import distove.voice.dto.request.UpdateSettingRequest;
import distove.voice.dto.response.IceCandidateResponse;
import distove.voice.dto.response.ParticipantLeftResponse;
import distove.voice.dto.response.SdpAnswerResponse;
import distove.voice.dto.response.UpdateSettingResponse;
import distove.voice.entity.IncomingParticipant;
import distove.voice.entity.Participant;
import distove.voice.entity.VoiceRoom;
import distove.voice.enumerate.MessageType;
import distove.voice.service.ParticipantService;
import distove.voice.service.VoiceRoomService;
import distove.voice.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static distove.voice.enumerate.MessageType.getMessageType;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignalingHandler extends TextWebSocketHandler {

    private final VoiceRoomService voiceRoomService;
    private final ParticipantService participantService;
    private final MessageUtil messageUtil;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Gson gson = new GsonBuilder().create();
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private static final String UDP = "UDP";
    private static final String HOST = "host";

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        final JsonObject jsonMessage = gson.fromJson(textMessage.getPayload(), JsonObject.class);
        MessageType message = getMessageType(jsonMessage.get("message").getAsString());
        switch (message) {
            case JOIN:
                JoinRoomRequest joinRoomRequest = mapper.readValue(textMessage.getPayload(), JoinRoomRequest.class);
                join(session, joinRoomRequest.getUserId(), joinRoomRequest.getChannelId());
                break;
            case LEAVE:
                leave(session);
                break;
            case SDP_OFFER:
                SdpOfferRequest sdpOfferRequest = mapper.readValue(textMessage.getPayload(), SdpOfferRequest.class);
                sdpOffer(session, sdpOfferRequest.getUserId(), sdpOfferRequest.getSdpOffer());
                break;
            case ON_ICE_CANDIDATE:
                SendIceCandidateRequest sendIceCandidateRequest = mapper.readValue(textMessage.getPayload(), SendIceCandidateRequest.class);
                onIceCandidate(session, sendIceCandidateRequest.getUserId(), sendIceCandidateRequest.getIceCandidate());
                break;
            case UPDATE_SETTING:
                UpdateSettingRequest updateSettingRequest = mapper.readValue(textMessage.getPayload(), UpdateSettingRequest.class);
                updateSetting(session, updateSettingRequest.getIsCameraOn(), updateSettingRequest.getIsMicOn());
                break;
            case RESET_ALL:
                preDestroy();
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public void join(WebSocketSession session, Long userId, Long channelId) {
        VoiceRoom voiceRoom = voiceRoomService.findByChannelId(channelId)
                .orElseGet(() -> voiceRoomService.create(channelId));

        WebRtcEndpoint outgoingEndpoint = createEndpoint(userId, voiceRoom.getPipeline(), session);
        participantService.join(userId, voiceRoom, outgoingEndpoint, session);
    }

    public void leave(WebSocketSession session) {
        Participant me = participantService.getByWebSocketSession(session);
        Long userId = me.getUserId();
        Long channelId = me.getVoiceRoom().getChannelId();

        participantService.leave(me);
        deleteIncomingEndpoint(userId, channelId);
    }

    public void sdpOffer(WebSocketSession session, Long senderUserId, String sdpOffer) {
        Participant me = participantService.getByWebSocketSession(session);
        Participant sender = participantService.getByUserId(senderUserId);

        WebRtcEndpoint incomingEndpoint = getIncomingEndpoint(me, sender);
        me.getIncomingParticipants().put(senderUserId, new IncomingParticipant(senderUserId, incomingEndpoint));

        SdpAnswerResponse sdpAnswerResponse = SdpAnswerResponse.of(senderUserId, incomingEndpoint.processOffer(sdpOffer));
        messageUtil.sendMessage(session, sdpAnswerResponse);

        incomingEndpoint.gatherCandidates();
        participantService.save(me);
    }

    public void onIceCandidate(WebSocketSession session, Long senderUserId, IceCandidate iceCandidateInfo) {
        String candidate = iceCandidateInfo.getCandidate();
        Participant sender = participantService.getByUserId(senderUserId);
        if (candidate.contains(UDP) && candidate.contains(HOST)) {
            Participant me = participantService.getByWebSocketSession(session);
            if (me.equals(sender)) {
                me.getEndpoint().addIceCandidate(iceCandidateInfo);
            } else {
                me.getIncomingParticipants().get(sender.getUserId()).getEndpoint().addIceCandidate(iceCandidateInfo);
            }
        }
    }

    public void updateSetting(WebSocketSession session, Boolean isCameraOn, Boolean isMicOn) {
        Participant me = participantService.getByWebSocketSession(session);
        List<Participant> participants = participantService.findAllByChannelId(me.getVoiceRoom().getChannelId());
        me.updateVideoSetting(isCameraOn, isMicOn);
        for (Participant participant : participants) {
            if (!participant.equals(me)) {
                UpdateSettingResponse updateSettingResponse = UpdateSettingResponse.of(me.getUserId(), me.getVideoSetting());
                messageUtil.sendMessage(participant.getSession(), updateSettingResponse);
            }
        }
    }

    public void preDestroy() {
        List<Participant> participants = participantService.findAll();
        List<VoiceRoom> voiceRooms = voiceRoomService.findAll();

        for (Participant participant : participants) {
            participant.getEndpoint().release();
            for (IncomingParticipant incomingParticipant : participant.getIncomingParticipants().values()) {
                incomingParticipant.getEndpoint().release();
            }
        }
        for (VoiceRoom voiceRoom : voiceRooms) {
            voiceRoom.getPipeline().release();
        }
        participantService.deleteAll();
        voiceRoomService.deleteAll();
    }

    private WebRtcEndpoint createEndpoint(Long userId, MediaPipeline pipeline, WebSocketSession session) {
        WebRtcEndpoint endpoint = new WebRtcEndpoint.Builder(pipeline).build();
        endpoint.addIceCandidateFoundListener(event -> {
            String candidate = event.getCandidate().getCandidate();
            if (candidate.contains(UDP) && candidate.contains(HOST)) {
                synchronized (session) {
                    IceCandidateResponse iceCandidateResponse = IceCandidateResponse.of(userId, event.getCandidate());
                    messageUtil.sendMessage(session, iceCandidateResponse);
                }
            }
        });
        return endpoint;
    }

    private void deleteIncomingEndpoint(Long userId, Long channelId) {
        List<Participant> participants = participantService.findAllByChannelId(channelId);
        if (!participants.isEmpty()) {
            for (Participant participant : participants) {
                messageUtil.sendMessage(participant.getSession(), ParticipantLeftResponse.of(userId));
                if (participant.getIncomingParticipants().containsKey(userId)) {
                    participant.getIncomingParticipants().get(userId).getEndpoint().release();
                    participant.getIncomingParticipants().remove(userId);
                }
            }
        } else {
            voiceRoomService.findByChannelId(channelId).ifPresent(voiceRoom -> {
                voiceRoom.getPipeline().release();
                voiceRoomService.delete(channelId);
            });
        }
    }

    private WebRtcEndpoint getIncomingEndpoint(Participant me, Participant sender) {
        if (me.getUserId().equals(sender.getUserId())) return me.getEndpoint();

        IncomingParticipant incomingParticipant = me.getIncomingParticipants().get(sender.getUserId());
        if (incomingParticipant == null) {
            WebRtcEndpoint incomingEndpoint = createEndpoint(
                    sender.getUserId(),
                    me.getVoiceRoom().getPipeline(),
                    me.getSession());
            incomingParticipant = new IncomingParticipant(sender.getUserId(), incomingEndpoint);
        }

        sender.getEndpoint().connect(incomingParticipant.getEndpoint());
        incomingParticipant.getEndpoint().gatherCandidates();
        return incomingParticipant.getEndpoint();
    }

}
