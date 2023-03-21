package distove.voice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import distove.voice.client.UserClient;
import distove.voice.client.dto.UserResponse;
import distove.voice.dto.request.JoinRoomRequest;
import distove.voice.dto.request.SdpOfferRequest;
import distove.voice.dto.request.SendIceCandidateRequest;
import distove.voice.dto.request.UpdateSettingRequest;
import distove.voice.dto.response.*;
import distove.voice.entity.IncomingParticipant;
import distove.voice.entity.Participant;
import distove.voice.entity.VideoSetting;
import distove.voice.entity.VoiceRoom;
import distove.voice.enumerate.MessageType;
import distove.voice.service.ParticipantService;
import distove.voice.service.VoiceRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static distove.voice.enumerate.MessageType.getMessageType;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignalingHandler extends TextWebSocketHandler {

    private final UserClient userClient;
    private final VoiceRoomService voiceRoomService;
    private final ParticipantService participantService;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Gson gson = new GsonBuilder().create();
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage response) throws Exception {
        final JsonObject jsonMessage = gson.fromJson(response.getPayload(), JsonObject.class);
        MessageType message = getMessageType(jsonMessage.get("message").getAsString());
        switch (message) {
            case JOIN_ROOM:
                JoinRoomRequest joinRoomRequest = mapper.readValue(response.getPayload(), JoinRoomRequest.class);
                joinRoom(joinRoomRequest.getUserId(), joinRoomRequest.getChannelId(), session);
                break;
            case LEAVE_ROOM:
                leaveRoom(session);
                break;
            case SDP_OFFER:
                SdpOfferRequest sdpOfferRequest = mapper.readValue(response.getPayload(), SdpOfferRequest.class);
                sdpOffer(session, sdpOfferRequest.getUserId(), sdpOfferRequest.getSdpOffer());
                break;
            case SEND_ICE_CANDIDATE:
                SendIceCandidateRequest sendIceCandidateRequest = mapper.readValue(response.getPayload(), SendIceCandidateRequest.class);
                sendIceCandidate(session, sendIceCandidateRequest.getUserId(), sendIceCandidateRequest.getIceCandidate());
                break;
            case UPDATE_SETTING:
                UpdateSettingRequest updateSettingRequest = mapper.readValue(response.getPayload(), UpdateSettingRequest.class);
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

    private void joinRoom(Long userId, Long channelId, WebSocketSession session) throws IOException {
        VoiceRoom voiceRoom = voiceRoomService.getByChannelId(channelId);
        WebRtcEndpoint outgoingMediaEndpoint = new WebRtcEndpoint.Builder(voiceRoom.getPipeline()).build();
        outgoingMediaEndpoint.addIceCandidateFoundListener(event -> {
            String candidate = event.getCandidate().getCandidate();
            if (candidate.contains("UDP") && candidate.contains("host")) {
                try {
                    synchronized (session) {
                        IceCandidateResponse iceCandidateResponse = IceCandidateResponse.of(userId, event.getCandidate());
                        sendMessage(session, iceCandidateResponse);
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            }
        });

        VideoSetting videoSetting = new VideoSetting(false, false);
        Participant me = new Participant(userId, voiceRoom, outgoingMediaEndpoint, session, videoSetting);
        participantService.add(me);

        List<Participant> participants = participantService.findAllByChannelId(voiceRoom.getChannelId());
        List<UserResponse> userResponses = getUsersByClient(participants);
        Map<Long, UserResponse> userResponsesMap = userResponses.stream().collect(Collectors.toMap(UserResponse::getId, x -> x));

        UserResponse user = userClient.getUser(me.getUserId());
        List<ParticipantResponse> participantResponses = new ArrayList<>();
        for (Participant participant : participants) {
            if (!participant.equals(me)) {
                ParticipantJoinedResponse participantJoinedResponse = ParticipantJoinedResponse.of(user, videoSetting);
                sendMessage(participant.getWebSocketSession(), participantJoinedResponse);

                UserResponse otherParticipant = userResponsesMap.get(participant.getUserId());
                participantResponses.add(ParticipantResponse.of(otherParticipant, participant.getVideoSetting()));
            }
        }
        sendMessage(me.getWebSocketSession(), ExistingParticipantsResponse.of(participantResponses));
    }

    private void leaveRoom(WebSocketSession session) throws IOException {
        Participant me = participantService.findByWebSocketSession(session);
        me.getMediaEndpoint().release();

        VoiceRoom voiceRoom = voiceRoomService.findByParticipant(me);
        Long userId = me.getUserId();

        List<Participant> participants = participantService.findAllByChannelId(voiceRoom.getChannelId());
        if (!participants.isEmpty()) {
            for (Participant participant : participants) {
                sendMessage(participant.getWebSocketSession(), ParticipantLeftResponse.of(userId));
                if (participant.getIncomingParticipants().containsKey(userId)) {
                    participant.getIncomingParticipants().get(userId).getMediaEndpoint().release();
                    participant.getIncomingParticipants().remove(userId);
                }
            }
        } else {
            voiceRoomService.close(voiceRoom);
        }
        participantService.delete(me);
    }

    private void sdpOffer(WebSocketSession session, Long senderUserId, String sdpOffer) throws IOException {
        Participant me = participantService.findByWebSocketSession(session);
        Participant sender = participantService.findByUserId(senderUserId);

        WebRtcEndpoint incomingMediaEndpointFromYou = getIncomingMediaEndpointFromYou(me, sender);
        me.getIncomingParticipants().put(senderUserId, new IncomingParticipant(senderUserId, incomingMediaEndpointFromYou));

        SdpAnswerResponse sdpAnswerResponse = SdpAnswerResponse.of(senderUserId, incomingMediaEndpointFromYou.processOffer(sdpOffer));
        sendMessage(session, sdpAnswerResponse);

        incomingMediaEndpointFromYou.gatherCandidates();
        participantService.save(me);
    }

    private void sendIceCandidate(WebSocketSession session, Long senderUserId, IceCandidate iceCandidateInfo) {
        String candidate = iceCandidateInfo.getCandidate();
        if (candidate.contains("UDP") && candidate.contains("host")) {
            Participant me = participantService.findByWebSocketSession(session);
            if (me.getUserId().equals(senderUserId)) {
                me.getMediaEndpoint().addIceCandidate(iceCandidateInfo);
            } else {
                me.getIncomingParticipants().get(senderUserId).getMediaEndpoint().addIceCandidate(iceCandidateInfo);
            }
        }
    }

    private void updateSetting(WebSocketSession session, Boolean isCameraOn, Boolean isMicOn) throws IOException {
        Participant me = participantService.findByWebSocketSession(session);
        List<Participant> participants = participantService.findAllByChannelId(me.getVoiceRoom().getChannelId());
        me.updateVideoInfo(new VideoSetting(isCameraOn, isMicOn));
        for (Participant participant : participants) {
            if (!participant.equals(me)) {
                UpdateSettingResponse updateSettingResponse = UpdateSettingResponse.of(me.getUserId(), new VideoSetting(isCameraOn, isMicOn));
                sendMessage(participant.getWebSocketSession(), updateSettingResponse);
            }
        }
    }

    private void preDestroy() {
        List<Participant> participants = participantService.findAll();
        List<VoiceRoom> voiceRooms = voiceRoomService.findAll();

        for (Participant participant : participants) {
            participant.getMediaEndpoint().release();
            for (IncomingParticipant incomingParticipant : participant.getIncomingParticipants().values()) {
                incomingParticipant.getMediaEndpoint().release();
            }
        }
        for (VoiceRoom voiceRoom : voiceRooms) {
            voiceRoom.getPipeline().release();
        }
        participantService.deleteAll();
        voiceRoomService.deleteAll();
    }

    private <T> void sendMessage(WebSocketSession session, T object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(object);
        session.sendMessage(new TextMessage(jsonInString));
    }

    private List<UserResponse> getUsersByClient(List<Participant> participants) {
        List<Long> userIds = participants.stream().map(Participant::getUserId).collect(Collectors.toList());
        return userClient.getUsers(userIds.toString().replace("[", "").replace("]", ""));
    }

    private WebRtcEndpoint getIncomingMediaEndpointFromYou(Participant participant, Participant sender) {
        if (participant.getUserId().equals(sender.getUserId())) return participant.getMediaEndpoint();

        IncomingParticipant incomingParticipant = participant.getIncomingParticipants().get(sender.getUserId());
        if (incomingParticipant == null) {
            WebRtcEndpoint incomingMediaEndpoint = new WebRtcEndpoint.Builder(participant.getVoiceRoom().getPipeline()).build();
            incomingMediaEndpoint.addIceCandidateFoundListener(event -> {
                if (event.getCandidate().getCandidate().contains("UDP") && event.getCandidate().getCandidate().contains("host")) {
                    try {
                        synchronized (participant.getWebSocketSession()) {
                            IceCandidateResponse iceCandidateResponse = IceCandidateResponse.of(sender.getUserId(), event.getCandidate());
                            sendMessage(participant.getWebSocketSession(), iceCandidateResponse);
                        }
                    } catch (IOException e) {
                        log.debug(e.getMessage());
                    }
                }

            });
            incomingParticipant = new IncomingParticipant(sender.getUserId(), incomingMediaEndpoint);
        }

        sender.getMediaEndpoint().connect(incomingParticipant.getMediaEndpoint());
        incomingParticipant.getMediaEndpoint().gatherCandidates();

        return incomingParticipant.getMediaEndpoint();
    }

}
