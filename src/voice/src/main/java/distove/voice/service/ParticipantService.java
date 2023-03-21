package distove.voice.service;

import distove.voice.client.UserClient;
import distove.voice.client.dto.UserResponse;
import distove.voice.dto.response.ExistingParticipantsResponse;
import distove.voice.dto.response.ParticipantJoinedResponse;
import distove.voice.dto.response.ParticipantResponse;
import distove.voice.entity.Participant;
import distove.voice.entity.VideoSetting;
import distove.voice.entity.VoiceRoom;
import distove.voice.exception.DistoveException;
import distove.voice.repository.ParticipantRepository;
import distove.voice.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static distove.voice.exception.ErrorCode.PARTICIPANT_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserClient userClient;
    private final MessageUtil messageUtil;

    public void join(Long userId, VoiceRoom voiceRoom, WebRtcEndpoint outgoingEndpoint, WebSocketSession session) {
        VideoSetting videoSetting = new VideoSetting(false, false);
        Participant me = new Participant(userId, voiceRoom, outgoingEndpoint, session, videoSetting);
        participantRepository.add(me);
        notifyNewParticipant(voiceRoom, videoSetting, me);
    }

    public Participant getByWebSocketSession(WebSocketSession session) {
        return participantRepository.findByWebSocketSession(session)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
    }

    public void leave(Participant participant) {
        participant.getEndpoint().release();
        participantRepository.delete(participant);
    }

    public Participant getByUserId(Long userId) {
        return participantRepository.findByUserId(userId)
                .orElseThrow(() -> new DistoveException(PARTICIPANT_NOT_FOUND_ERROR));
    }

    public List<Participant> findAllByChannelId(Long channelId) {
        return participantRepository.findAllByChannelId(channelId);
    }

    public void save(Participant me) {
        participantRepository.save(me.getUserId(), me);
    }

    public List<Participant> findAll() {
        return participantRepository.findAll();
    }

    public void deleteAll() {
        participantRepository.deleteAll();
    }

    private void notifyNewParticipant(VoiceRoom voiceRoom, VideoSetting videoSetting, Participant me) {
        List<Participant> participants = participantRepository.findAllByChannelId(voiceRoom.getChannelId());
        List<UserResponse> userResponses = getUsersByClient(participants);
        Map<Long, UserResponse> userResponsesMap = userResponses.stream().collect(Collectors.toMap(UserResponse::getId, x -> x));

        UserResponse user = userClient.getUser(me.getUserId());
        List<ParticipantResponse> participantResponses = new ArrayList<>();

        try {
            for (Participant participant : participants) {
                if (!participant.equals(me)) {
                    ParticipantJoinedResponse participantJoinedResponse = ParticipantJoinedResponse.of(user, videoSetting);
                    messageUtil.sendMessage(participant.getSession(), participantJoinedResponse);

                    UserResponse otherParticipant = userResponsesMap.get(participant.getUserId());
                    participantResponses.add(ParticipantResponse.of(otherParticipant, participant.getVideoSetting()));
                }
            }
            messageUtil.sendMessage(me.getSession(), ExistingParticipantsResponse.of(participantResponses));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<UserResponse> getUsersByClient(List<Participant> participants) {
        List<Long> userIds = participants.stream().map(Participant::getUserId).collect(Collectors.toList());
        return userClient.getUsers(userIds.toString().replace("[", "").replace("]", ""));
    }

}
