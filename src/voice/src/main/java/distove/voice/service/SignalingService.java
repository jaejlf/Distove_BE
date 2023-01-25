package distove.voice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import distove.voice.dto.response.ExistingParticipantsResponse;
import distove.voice.dto.response.IceCandidateResponse;
import distove.voice.dto.response.NewParticipantArrivedResponse;
import distove.voice.dto.response.SdpAnswerResponse;
import distove.voice.entity.IncomingParticipant;
import distove.voice.entity.Participant;
import distove.voice.entity.Room;
import distove.voice.exception.DistoveException;
import distove.voice.repository.ParticipantRepository;
import distove.voice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.*;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;

import static distove.voice.exception.ErrorCode.PARTICIPANT_NOT_FOUND_ERROR;
import static distove.voice.exception.ErrorCode.ROOM_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalingService {
    private final KurentoClient kurentoClient;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;


    public <T> TextMessage toJson(T object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(object);
        return new TextMessage(jsonInString);
    }

    public void joinRoom(Long userId, Long channelId, WebSocketSession webSocketSession) throws IOException {

        Room room = getRoomByChannelId(channelId);
        WebRtcEndpoint outgoingMediaEndpoint = new WebRtcEndpoint.Builder(room.getPipeline()).build();
        outgoingMediaEndpoint.addIceCandidateFoundListener(event -> {
            try {
                synchronized (webSocketSession) {
                    webSocketSession.sendMessage(toJson(new IceCandidateResponse(userId, event.getCandidate())));
                }
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        });
        Participant participant = new Participant(userId, room, webSocketSession, outgoingMediaEndpoint);
        List<Participant> participants = participantRepository.findParticipantsByRoom(room);

        if (!participants.isEmpty()) {
            for (final Participant otherParticipant : participants) {
                otherParticipant.getWebSocketSession().sendMessage(toJson(new NewParticipantArrivedResponse(participant)));
            }
            participant.getWebSocketSession().sendMessage(toJson(new ExistingParticipantsResponse(participants)));
        }
        participantRepository.insert(participant);
    }

    public Room getRoomByChannelId(Long channelId) {
        Room room = roomRepository.findRoomByChannelId(channelId)
                .orElse(createNewRoomByChannelId(channelId));
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
        participant.getIncomingParticipants().add(new IncomingParticipant(senderUserId, incomingMediaEndpointFromYou));
        webSocketSession.sendMessage(toJson(new SdpAnswerResponse(senderUserId, incomingMediaEndpointFromYou.processOffer(sdpOffer))));
        incomingMediaEndpointFromYou.gatherCandidates();
    }

    private WebRtcEndpoint getIncomingMediaEndpointFromYou(Participant participant, Participant sender) {
        if (participant.getUserId().equals(sender.getUserId())) {
            return participant.getMediaEndpoint();
        }
        WebRtcEndpoint incomingMediaEndpoint = participant.getIncomingParticipants().stream()
                .filter(incomingParticipant -> incomingParticipant.getUserId().equals(sender.getUserId())).findFirst().get().getMediaEndpoint();
        if (incomingMediaEndpoint == null) {
            incomingMediaEndpoint = new WebRtcEndpoint.Builder(participant.getRoom().getPipeline()).build();
            incomingMediaEndpoint.addIceCandidateFoundListener(event -> {
                try {
                    synchronized (participant.getWebSocketSession()) {
                        participant.getWebSocketSession().sendMessage(toJson(new IceCandidateResponse(sender.getUserId(), event.getCandidate())));
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            });
        }
        sender.getMediaEndpoint().connect(incomingMediaEndpoint);
        return incomingMediaEndpoint;
    }


    public void leaveRoom(WebSocketSession webSocketSession) throws IOException {
        Participant participant = participantRepository.findParticipantByWebSocketSession(webSocketSession)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
        participant.getMediaEndpoint().release();
        participantRepository.deleteParticipant(participant);
        Room room = roomRepository.findRoomById(participant.getRoom().getId())
                .orElseThrow(() -> new DistoveException(ROOM_NOT_FOUND_ERROR));

        List<Participant> participants = participantRepository.findParticipantsByRoom(room);
        if (!participants.isEmpty()) {
            for (Participant otherParticipant : participants) {
                otherParticipant.getIncomingParticipants()
                        .removeIf(incomingParticipant -> {
                                    incomingParticipant.getMediaEndpoint().release();
                                    return incomingParticipant.getUserId().equals(participant.getUserId());
                                }
                        );
            }
        } else {
            closeRoom(room);
        }
    }

    public void closeRoom(Room room) {
        room.getPipeline().release();
        roomRepository.deleteById(room.getId());
    }

    public void addIceCandidate(WebSocketSession webSocketSession, Long senderUserId, IceCandidate iceCandidateInfo) {
        Participant participant = participantRepository.findParticipantByWebSocketSession(webSocketSession)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
        Participant sender = participantRepository.findParticipantByUserId(senderUserId)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
        if (participant.getUserId().equals(senderUserId)) {
            participant.getMediaEndpoint().addIceCandidate(iceCandidateInfo);
        } else {
            participant.getIncomingParticipants().stream()
                    .filter(incomingParticipant -> incomingParticipant.getUserId().equals(senderUserId)).findFirst().get()
                    .getMediaEndpoint().addIceCandidate(iceCandidateInfo);
        }
    }

    @PreDestroy
    public void preDestroy() {
        List<Participant> participants = participantRepository.findAll();
        List<Room> rooms = roomRepository.findAll();

        for (Participant participant : participants) {
            participant.getMediaEndpoint().release();
            for (IncomingParticipant incomingParticipant : participant.getIncomingParticipants()) {
                incomingParticipant.getMediaEndpoint().release();
            }
        }
        for (Room room : rooms) {
            room.getPipeline().release();
        }

    }
}
