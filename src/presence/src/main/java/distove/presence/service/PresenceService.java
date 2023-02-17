package distove.presence.service;

import distove.presence.dto.Presence;
import distove.presence.dto.PresenceTime;
import distove.presence.dto.response.PresenceResponse;
import distove.presence.dto.response.PresenceUpdateResponse;
import distove.presence.enumerate.PresenceType;
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

    public void updateUserPresence(Long userId) {

        if (presenceRepository.isUserOnline(userId)) {
            presenceRepository.removePresenceByUserId(userId);

        } else {
            List<Long> serverIds = communityClient.getServerIdsByUserId(userId);
            for (Long serverId : serverIds) {
                simpMessagingTemplate.convertAndSend("/sub/" + serverId, PresenceUpdateResponse.of(userId, PresenceType.ONLINE.getPresence()));
            }
        }


        presenceRepository.save(userId, newPresenceTime(PresenceType.ONLINE.getPresence()));

    }


    @Scheduled(cron = "0/20 * * * * ?") // 1분
    public void setInactiveUsersToAway() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Map<Long, PresenceTime> presenceTimeMap = presenceRepository.findAll();
        List<Long> awayUserIds = new ArrayList<>();
        for (Long userId : presenceTimeMap.keySet()) {
            if (currentTime.getTime() > (presenceTimeMap.get(userId).getActiveAt().getTime() + 30000)) {
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
