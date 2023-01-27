package distove.voice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import distove.voice.dto.request.AddIceCandidateRequest;
import distove.voice.dto.request.JoinRoomRequest;
import distove.voice.dto.request.SdpOfferRequest;
import distove.voice.service.SignalingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
@Controller
@RequiredArgsConstructor
public class SignalingController extends TextWebSocketHandler {
    private final SignalingService signalingService;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Gson gson = new GsonBuilder().create();

    //for test
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession webSocketSession, TextMessage response) throws Exception {
        final JsonObject jsonMessage = gson.fromJson(response.getPayload(), JsonObject.class);
        switch (jsonMessage.get("type").getAsString()) {
            case "joinRoom":
                JoinRoomRequest joinRoomRequest = mapper.readValue(response.getPayload(), JoinRoomRequest.class);
                signalingService.joinRoom(joinRoomRequest.getUserId(), joinRoomRequest.getChannelId(), webSocketSession);
                break;
            case "sdpOffer":
                SdpOfferRequest sdpOfferRequest = mapper.readValue(response.getPayload(), SdpOfferRequest.class);
                signalingService.sdpOffer(webSocketSession, sdpOfferRequest.getUserId(), sdpOfferRequest.getSdpOffer());
                break;
            case "leaveRoom":
                signalingService.leaveRoom(webSocketSession);
                break;
            case "addIceCandidate":
                AddIceCandidateRequest addIceCandidateRequest = mapper.readValue(response.getPayload(), AddIceCandidateRequest.class);
                signalingService.addIceCandidate(webSocketSession, addIceCandidateRequest.getUserId(), addIceCandidateRequest.getCandidateInfo());
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        sessions.add(webSocketSession);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {
        signalingService.leaveRoom(webSocketSession);
    }

}