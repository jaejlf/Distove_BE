package distove.presence.service;

import distove.presence.client.CommunityClient;
import distove.presence.client.dto.UserResponse;
import distove.presence.dto.response.PresenceResponse;
import distove.presence.entity.Presence;
import distove.presence.enumerate.PresenceType;
import distove.presence.repository.ConnectionRepository;
import distove.presence.repository.PresenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static distove.presence.enumerate.PresenceType.*;

@Service
@Slf4j
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
        PresenceType presenceType = getPresenceType(type);
        switch (presenceType) {
            case ONLINE:
                if (!presenceRepository.isAway(userId)) presenceRepository.deleteByUserId(userId);
                else publishPresence(userId, ONLINE); // AWAY 상태였을 경우에만 publish
                presenceRepository.save(userId, new Presence(ONLINE));
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
            case OFFLINE:
                if (!presenceRepository.isAway(userId)) presenceRepository.deleteByUserId(userId);
                connectionRepository.deleteByUserId(userId);
                publishPresence(userId, OFFLINE);
                break;
            default:
                break;
        }
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void updateInactiveUsersToAway() {
        Map<Long, Presence> presenceMap = presenceRepository.findAll();
        for (Long userId : presenceMap.keySet()) {
            Presence presence = presenceMap.get(userId);
            if (isInactive(presence)) {
                publishPresence(userId, AWAY);
                presenceRepository.deleteByUserId(userId);
            }
        }
    }

    private boolean isInactive(Presence presence) {
        if (presence.getPresenceType().equals(VOICE_ON)) return false;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime activeAt = presence.getActiveAt();
        return now.isAfter(activeAt.plusMinutes(10));
    }

    private void publishPresence(Long userId, PresenceType presenceType) {
        List<Long> serverIds = communityClient.getServerIdsByUserId(userId);
        for (Long serverId : serverIds) {
            simpMessagingTemplate.convertAndSend(destination + serverId, PresenceResponse.update(userId, presenceType));
        }
    }

}
