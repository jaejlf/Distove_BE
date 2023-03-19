package distove.voice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import distove.voice.dto.request.SendIceCandidateRequest;
import distove.voice.dto.request.JoinRoomRequest;
import distove.voice.dto.request.SdpOfferRequest;
import distove.voice.dto.request.UpdateSettingRequest;
import distove.voice.enumerate.MessageType;
import distove.voice.service.SignalingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static distove.voice.enumerate.MessageType.*;

@Controller
@RequiredArgsConstructor
public class SignalingController extends TextWebSocketHandler {

    private final SignalingService signalingService;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Gson gson = new GsonBuilder().create();
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession webSocketSession, TextMessage response) throws Exception {
        final JsonObject jsonMessage = gson.fromJson(response.getPayload(), JsonObject.class);
        MessageType message = getMessageType(jsonMessage.get("message").getAsString());
        switch (message) {
            case JOIN_ROOM:
                JoinRoomRequest joinRoomRequest = mapper.readValue(response.getPayload(), JoinRoomRequest.class);
                signalingService.joinRoom(joinRoomRequest.getUserId(), joinRoomRequest.getChannelId(), webSocketSession);
                break;
            case LEAVE_ROOM:
                signalingService.leaveRoom(webSocketSession);
                break;
            case SDP_OFFER:
                SdpOfferRequest sdpOfferRequest = mapper.readValue(response.getPayload(), SdpOfferRequest.class);
                signalingService.sdpOffer(webSocketSession, sdpOfferRequest.getUserId(), sdpOfferRequest.getSdpOffer());
                break;
            case SEND_ICE_CANDIDATE:
                SendIceCandidateRequest sendIceCandidateRequest = mapper.readValue(response.getPayload(), SendIceCandidateRequest.class);
                signalingService.sendIceCandidate(webSocketSession, sendIceCandidateRequest.getUserId(), sendIceCandidateRequest.getIceCandidate());
                break;
            case UPDATE_SETTING:
                UpdateSettingRequest updateSettingRequest = mapper.readValue(response.getPayload(), UpdateSettingRequest.class);
                signalingService.updateSetting(webSocketSession, updateSettingRequest.getIsCameraOn(), updateSettingRequest.getIsMicOn());
                break;
            case RESET_ALL:
                signalingService.preDestroy();
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        sessions.add(webSocketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) {
        sessions.remove(webSocketSession);
    }

}
