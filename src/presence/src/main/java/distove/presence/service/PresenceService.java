package distove.presence.service;

import distove.presence.dto.Presence;
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
import java.util.ArrayList;
import java.util.List;

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

            Presence presence = userConnectionRepository.isUserConnected(user.getId())
                    ? presenceRepository.findPresenceByUserId(user.getId())
                    .orElseGet(() -> PresenceType.AWAY.getPresence())
                    : PresenceType.OFFLINE.getPresence();
            log.info("{}",presence);
            presenceResponses.add(PresenceResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl(), presence));
        }

        return presenceResponses;
    }

    public void updateUserPresence(Long userId) {

        if (presenceRepository.isUserOnline(userId)) {
            presenceRepository.removePresenceByUserId(userId);
        }
        presenceRepository.save(userId, newPresenceTime(PresenceType.ONLINE.getPresence()));
    }

//    public void subscribeServerPresence(Long userId,Long serverId){
//        userConnectionRepository.addUserConnection(userId);
//    }

    @Scheduled(cron = "0/60 * * * * ?") // 1분
    public void setInactiveUsersToAway() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        log.info("is Scheduler running {}",currentTime);

        presenceRepository.findAll().forEach((userId,presenceTime)->{
            if(currentTime.getTime()>(presenceTime.getActiveAt().getTime()+600000)){
                List<Long> serverIds = communityClient.getServerIdsByUserId(userId);
                for (Long serverId : serverIds) {
                    simpMessagingTemplate.convertAndSend( "/sub/" + serverId, PresenceUpdateResponse.of(userId,PresenceType.AWAY.getPresence()));
                }
                presenceRepository.removePresenceByUserId(userId);
            }
        });
    }

}
