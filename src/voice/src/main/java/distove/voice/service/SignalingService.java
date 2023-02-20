package distove.voice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import distove.voice.dto.response.UpdatedVideoInfoResponse;
import distove.voice.dto.response.UserVideoResponse;
import distove.voice.entity.IncomingParticipant;
import distove.voice.entity.Participant;
import distove.voice.entity.Room;
import distove.voice.entity.VideoInfo;
import distove.voice.enumerate.ServiceInfoType;
import distove.voice.exception.DistoveException;
import distove.voice.repository.ParticipantRepository;
import distove.voice.repository.RoomRepository;
import distove.voice.web.PresenceClient;
import distove.voice.web.UserClient;
import distove.voice.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.IceCandidate;
import org.kurento.client.KurentoClient;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static distove.voice.dto.response.ExistingParticipantsResponse.newExistingParticipantsResponse;
import static distove.voice.dto.response.IceCandidateResponse.newIceCandidateResponse;
import static distove.voice.dto.response.LeftRoomResponse.newLeftRoomResponse;
import static distove.voice.dto.response.NewParticipantArrivedResponse.newNewParticipantArrivedResponse;
import static distove.voice.dto.response.SdpAnswerResponse.newSdpAnswerResponse;
import static distove.voice.entity.IncomingParticipant.newIncomingParticipant;
import static distove.voice.exception.ErrorCode.PARTICIPANT_NOT_FOUND_ERROR;
import static distove.voice.exception.ErrorCode.ROOM_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalingService {
    private final KurentoClient kurentoClient;
    private final UserClient userClient;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final PresenceClient presenceClient;


    public <T> TextMessage toJson(T object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(object);
        return new TextMessage(jsonInString);
    }

    public void joinRoom(Long userId, Long channelId, WebSocketSession webSocketSession, Boolean isCameraOn, Boolean isMicOn) throws IOException {
        Room room = getRoomByChannelId(channelId);
        VideoInfo videoInfo = VideoInfo.of(isCameraOn, isMicOn);
        WebRtcEndpoint outgoingMediaEndpoint = new WebRtcEndpoint.Builder(room.getPipeline()).build();
        outgoingMediaEndpoint.addIceCandidateFoundListener(event -> {
            try {
                synchronized (webSocketSession) {
                    webSocketSession.sendMessage(toJson(newIceCandidateResponse(userId, event.getCandidate())));
                }
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        });
        presenceClient.updateUserPresence(userId, ServiceInfoType.VOICE_ON.getType());
        Participant participant = Participant.of(userId, room, outgoingMediaEndpoint, webSocketSession, videoInfo);
        List<Participant> participants = participantRepository.findParticipantsByChannelId(room.getChannelId());
        List<UserResponse> userResponses = userClient.getUsers(participants.stream().map(x->x.getUserId()).collect(Collectors.toList()).toString().replace("[","").replace("]",""));
        Map<Long,UserResponse> userResponsesMap = userResponses.stream().collect(Collectors.toMap(u->u.getId(), u->u));

        UserResponse user = userClient.getUser(participant.getUserId());

        participantRepository.insert(participant);
        List<UserVideoResponse> users = new ArrayList<>();

        for (Participant otherParticipant : participants) {
            if (!otherParticipant.equals(participant)) {
                otherParticipant.getWebSocketSession().sendMessage(toJson(newNewParticipantArrivedResponse(UserVideoResponse.of(user.getId(),user.getNickname(),user.getProfileImgUrl(),videoInfo))));

                UserResponse otherParticipantUserResponse = userResponsesMap.get(otherParticipant.getUserId());
                users.add(UserVideoResponse.of(
                        otherParticipant.getUserId(),
                        otherParticipantUserResponse.getNickname(),
                        otherParticipantUserResponse.getProfileImgUrl(),
                        otherParticipant.getVideoInfo()));
            }
        }

        participant.getWebSocketSession().sendMessage(toJson(newExistingParticipantsResponse(users)));
    }

    public Room getRoomByChannelId(Long channelId) {
        Room room = roomRepository.findRoomByChannelId(channelId).orElseGet(() -> createNewRoomByChannelId(channelId));
        return room;
    }

    public Room createNewRoomByChannelId(Long channelId) {
        Room room = roomRepository.save(new Room(channelId, kurentoClient.createMediaPipeline()));
        return room;
    }


    public void sdpOffer(WebSocketSession webSocketSession, Long senderUserId, String sdpOffer) throws IOException {

        Participant participant = participantRepository.findParticipantByWebSocketSession(webSocketSession)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
        Participant sender = participantRepository.findParticipantByUserId(senderUserId)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));

        WebRtcEndpoint incomingMediaEndpointFromYou = getIncomingMediaEndpointFromYou(participant, sender);
        participant.getIncomingParticipants()
                .put(senderUserId, newIncomingParticipant(senderUserId, incomingMediaEndpointFromYou));
        webSocketSession.sendMessage(toJson(newSdpAnswerResponse(senderUserId, incomingMediaEndpointFromYou.processOffer(sdpOffer))));
        incomingMediaEndpointFromYou.gatherCandidates();
        participantRepository.save(participant.getUserId(), participant);
    }

    private WebRtcEndpoint getIncomingMediaEndpointFromYou(Participant participant, Participant sender) {
        if (participant.getUserId().equals(sender.getUserId())) {
            return participant.getMediaEndpoint();
        }

        IncomingParticipant incomingParticipant = participant.getIncomingParticipants().get(sender.getUserId());
        if (incomingParticipant == null) {
            WebRtcEndpoint incomingMediaEndpoint = new WebRtcEndpoint.Builder(participant.getRoom()
                    .getPipeline()).build();
            incomingMediaEndpoint.addIceCandidateFoundListener(event -> {
                try {
                    synchronized (participant.getWebSocketSession()) {
                        participant.getWebSocketSession()
                                .sendMessage(toJson(newIceCandidateResponse(sender.getUserId(), event.getCandidate())));
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            });
            incomingParticipant = newIncomingParticipant(sender.getUserId(), incomingMediaEndpoint);
        }

        sender.getMediaEndpoint().connect(incomingParticipant.getMediaEndpoint());
        incomingParticipant.getMediaEndpoint().gatherCandidates();

        return incomingParticipant.getMediaEndpoint();
    }


    public void leaveRoom(WebSocketSession webSocketSession) throws IOException {
        Participant participant = participantRepository.findParticipantByWebSocketSession(webSocketSession)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
        participant.getMediaEndpoint().release();
        Room room = roomRepository.findRoomByChannelId(participant.getRoom().getChannelId())
                .orElseThrow(() -> new DistoveException(ROOM_NOT_FOUND_ERROR));
        presenceClient.updateUserPresence(participant.getUserId(), ServiceInfoType.VOICE_OFF.getType());

        List<Participant> participants = participantRepository.findParticipantsByChannelId(room.getChannelId());

        if (!participants.isEmpty()) {
            for (Participant otherParticipant : participants) {
                otherParticipant.getWebSocketSession()
                        .sendMessage(toJson(newLeftRoomResponse(participant.getUserId())));
                if (otherParticipant.getIncomingParticipants().containsKey(participant.getUserId())) {
                    otherParticipant.getIncomingParticipants().get(participant.getUserId()).getMediaEndpoint()
                            .release();
                    otherParticipant.getIncomingParticipants().remove(participant.getUserId());
                }
            }
        } else {
            closeRoom(room);
        }
        participantRepository.deleteParticipant(participant);


    }

    public void closeRoom(Room room) {
        room.getPipeline().release();
        roomRepository.deleteByChannelId(room.getChannelId());
    }

    public void addIceCandidate(WebSocketSession webSocketSession, Long senderUserId, IceCandidate iceCandidateInfo) {
        Participant participant = participantRepository.findParticipantByWebSocketSession(webSocketSession)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
        Participant sender = participantRepository.findParticipantByUserId(senderUserId)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
        if (participant.getUserId().equals(senderUserId)) {
            participant.getMediaEndpoint().addIceCandidate(iceCandidateInfo);
        } else {
            participant.getIncomingParticipants().get(senderUserId).getMediaEndpoint()
                    .addIceCandidate(iceCandidateInfo);
        }
    }


    public void updateVideoInfoRequest(WebSocketSession webSocketSession, Boolean isCameraOn, Boolean isMicOn) throws IOException {
        Participant me = participantRepository.findParticipantByWebSocketSession(webSocketSession)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
        List<Participant> participants = participantRepository.findParticipantsByChannelId(me.getRoom().getChannelId());
        me.updateVideoInfoOfParticipant(VideoInfo.of(isCameraOn, isMicOn));
        for (Participant participant : participants) {
            if (!participant.equals(me)) {
                participant.getWebSocketSession().sendMessage(toJson(UpdatedVideoInfoResponse.of(me.getUserId(),VideoInfo.of(isCameraOn,isMicOn))));
            }
        }

    }

    public void preDestroy() {
        List<Participant> participants = participantRepository.findAll();
        List<Room> rooms = roomRepository.findAll();

        for (Participant participant : participants) {
            participant.getMediaEndpoint().release();
            for (IncomingParticipant incomingParticipant : participant.getIncomingParticipants().values()) {
                incomingParticipant.getMediaEndpoint().release();
            }
        }
        for (Room room : rooms) {
            room.getPipeline().release();
        }
        participantRepository.deleteAllParticipant();
        roomRepository.deleteAllRoom();
    }
}
