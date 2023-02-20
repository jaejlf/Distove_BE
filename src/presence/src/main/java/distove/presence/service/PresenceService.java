package distove.presence.service;

import distove.presence.dto.Presence;
import distove.presence.dto.PresenceTime;
import distove.presence.dto.response.PresenceResponse;
import distove.presence.dto.response.PresenceUpdateResponse;
import distove.presence.enumerate.PresenceStatus;
import distove.presence.enumerate.PresenceType;
import distove.presence.exception.DistoveException;
import distove.presence.repository.PresenceRepository;
import distove.presence.repository.UserConnectionRepository;
import distove.presence.web.CommunityClient;
import distove.presence.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

import static distove.presence.dto.PresenceTime.newPresenceTime;
import static distove.presence.exception.ErrorCode.SERVICE_INFO_TYPE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceService {

    private final CommunityClient communityClient;
    private final PresenceRepository presenceRepository;
    private final UserConnectionRepository userConnectionRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<PresenceResponse> getMemberPresencesByServerId(Long serverId) {
        List<UserResponse> userResponses = communityClient.getUsersByServerId(serverId);
        List<PresenceResponse> presenceResponses = new ArrayList<>();

        for (UserResponse user : userResponses) {
            Presence presence=null;
            if(userConnectionRepository.isUserConnected(user.getId())){
                presence= presenceRepository.findPresenceByUserId(user.getId()).orElseGet(() -> newPresenceTime(PresenceType.AWAY.getPresence())).getPresence();
            }

            presenceResponses.add(PresenceResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl(), presence!=null?presence:PresenceType.OFFLINE.getPresence()));
        }

        return presenceResponses;
    }

    public void updateUserPresence(Long userId,String serviceInfo) {

        switch (serviceInfo){
            case "voiceOn":
                updateCallOnline(userId);
                break;
            case "voiceOff":
            case "chat":
            case "community":
                updateOnline(userId);
                break;
            default:
                throw new DistoveException(SERVICE_INFO_TYPE_ERROR);
        }


    }
    private void updateOnline(Long userId){
        if (presenceRepository.isUserOnline(userId)) {
            presenceRepository.removePresenceByUserId(userId);
        } else {
            sendUserPresence(userId,PresenceType.ONLINE);
        }
        presenceRepository.save(userId, newPresenceTime(PresenceType.ONLINE.getPresence()));

    }
    private void updateCallOnline(Long userId){
        sendUserPresence(userId,PresenceType.ONLINE_CALL);
        if (presenceRepository.isUserOnline(userId)) {
            presenceRepository.removePresenceByUserId(userId);
        }
        presenceRepository.save(userId, newPresenceTime(PresenceType.ONLINE_CALL.getPresence()));
    }
    public void sendUserPresence(Long userId,PresenceType presenceType){
        List<Long> serverIds = communityClient.getServerIdsByUserId(userId);
        for (Long serverId : serverIds) {
            simpMessagingTemplate.convertAndSend("/sub/" + serverId, PresenceUpdateResponse.of(userId, presenceType.getPresence()));
        }
    }

    public void sendNewUserConnectionEvent(Long userId, PresenceType presenceType){
        List<Long> serverIds = communityClient.getServerIdsByUserId(userId);
        for (Long serverId : serverIds) {
            simpMessagingTemplate.convertAndSend("/sub/" + serverId, PresenceUpdateResponse.of(userId, presenceType.getPresence()));
        }

    }

    @Scheduled(cron = "0/20 * * * * ?") // 1분
    public void setInactiveUsersToAway() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Map<Long, PresenceTime> presenceTimeMap = presenceRepository.findAll();
        List<Long> awayUserIds = new ArrayList<>();
        for (Long userId : presenceTimeMap.keySet()) {
            if ((currentTime.getTime() > (presenceTimeMap.get(userId).getActiveAt().getTime() + 30000))&& presenceTimeMap.get(userId).getPresence().getStatus()!= PresenceStatus.ONLINE_CALL) {
                List<Long> serverIds = communityClient.getServerIdsByUserId(userId);
                for (Long serverId : serverIds) {
                    simpMessagingTemplate.convertAndSend("/sub/" + serverId, PresenceUpdateResponse.of(userId, PresenceType.AWAY.getPresence()));
                }
                awayUserIds.add(userId);
            }
        }
        for (Long awayUserId : awayUserIds) {
            presenceRepository.removePresenceByUserId(awayUserId);
        }


    }
}
