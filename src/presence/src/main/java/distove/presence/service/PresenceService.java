package distove.presence.service;

import distove.presence.client.CommunityClient;
import distove.presence.client.dto.UserResponse;
import distove.presence.dto.response.PresenceResponse;
import distove.presence.entity.Presence;
import distove.presence.enumerate.PresenceType;
import distove.presence.enumerate.ServiceInfo;
import distove.presence.repository.ConnectionRepository;
import distove.presence.repository.PresenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static distove.presence.enumerate.PresenceType.*;
import static distove.presence.enumerate.ServiceInfo.getServiceInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceService {

    private final CommunityClient communityClient;
    private final PresenceRepository presenceRepository;
    private final ConnectionRepository connectionRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${sub.destination}")
    private String destination;

    public List<PresenceResponse> getMemberPresences(Long serverId) {
        List<UserResponse> userResponses = communityClient.getUsersByServerId(serverId);

        List<PresenceResponse> presenceResponses = new ArrayList<>();
        for (UserResponse user : userResponses) {
            if (connectionRepository.isConnected(user.getId())) {
                Presence presence = presenceRepository.findByUserId(user.getId())
                        .orElseGet(() -> new Presence(AWAY));
                presenceResponses.add(PresenceResponse.of(user, presence.getPresenceType()));
            } else {
                presenceResponses.add(PresenceResponse.of(user, new Presence(OFFLINE).getPresenceType()));
            }
        }
        return presenceResponses;
    }

    public void updatePresence(Long userId, String type) {
        ServiceInfo serviceInfo = getServiceInfo(type);
        switch (serviceInfo) {
            case VOICE_ON:
                if (!presenceRepository.isAway(userId)) presenceRepository.deleteByUserId(userId);
                presenceRepository.save(userId, new Presence(VOICE_ON));
                publishPresence(userId, VOICE_ON);
                break;
            case VOICE_OFF:
                presenceRepository.deleteByUserId(userId);
                presenceRepository.save(userId, new Presence(ONLINE)); // 화상 통화 종료 후 -> ONLINE 상태로 전환
                publishPresence(userId, ONLINE);
                break;
            case CHAT:
            case COMMUNITY:
                if (!presenceRepository.isAway(userId)) presenceRepository.deleteByUserId(userId);
                else publishPresence(userId, ONLINE); // AWAY 상태였을 경우에만 publish
                presenceRepository.save(userId, new Presence(ONLINE));
                break;
            case CONNECT:
                publishPresence(userId, ONLINE);
                break;
            case DISCONNECT:
                publishPresence(userId, OFFLINE);
                break;
            default:
                break;
        }
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void updateInactiveUsersToAway() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Map<Long, Presence> presenceMap = presenceRepository.findAll();
        List<Long> awayUserIds = new ArrayList<>();
        for (Long userId : presenceMap.keySet()) {
            if (isInactive(currentTime, presenceMap, userId)) {
                List<Long> serverIds = communityClient.getServerIdsByUserId(userId);
                for (Long serverId : serverIds) {
                    simpMessagingTemplate.convertAndSend(destination + serverId, PresenceResponse.update(userId, AWAY));
                }
                awayUserIds.add(userId);
            }
        }
        for (Long awayUserId : awayUserIds) {
            presenceRepository.deleteByUserId(awayUserId);
        }
    }

    private static boolean isInactive(Timestamp currentTime, Map<Long, Presence> presenceMap, Long userId) {
        return (currentTime.getTime() > (presenceMap.get(userId).getActiveAt().getTime() + 30000))
                && !Objects.equals(presenceMap.get(userId).getPresenceType().getStatus(), VOICE_ON.getStatus());
    }

    private void publishPresence(Long userId, PresenceType presenceType) {
        List<Long> serverIds = communityClient.getServerIdsByUserId(userId);
        for (Long serverId : serverIds) {
            simpMessagingTemplate.convertAndSend(destination + serverId, PresenceResponse.update(userId, presenceType));
        }
    }

}
