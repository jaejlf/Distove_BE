package distove.chat.service;

import distove.chat.client.CommunityClient;
import distove.chat.client.dto.CategoryInfoResponse;
import distove.chat.dto.response.NotificationResponse;
import distove.chat.entity.Connection;
import distove.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConnectionService connectionService;
    private final MessageRepository messageRepository;
    private final CommunityClient communityClient;

    @Value("${sub.destination}")
    private String destination;

    /**
     * 모든 채널의 알림 정보 업데이트
     *
     * @when 서버 구독 이벤트 발생 시
     * @when 채널 이동 시
     * @SUBSCRIBE /sub/notification/server/:serverId
     */
    public void notifyUnreadOfChannels(Long userId, Long serverId) {
        List<Long> channelIds = getAllChannelsInServer(userId, serverId);
        List<CategoryInfoResponse> categoryInfoResponses = communityClient.getCategoryIds(
                channelIds.toString().replaceAll("[\\[\\]]", ""));
        NotificationResponse notification = NotificationResponse.ofUnreads(serverId, categoryInfoResponses);
        simpMessagingTemplate.convertAndSend(destination + "server/" + serverId + "/" + userId, notification);
    }

    /**
     * 새로운 메시지 발행 알림
     *
     * @SUBSCRIBE /sub/notification/server/:serverId
     */
    public void notifyNewMessage(Long channelId) {
        Long serverId = connectionService.getConnection(channelId).getServerId();
        NotificationResponse notification = NotificationResponse.ofNewMessage(serverId, communityClient.getCategoryId(channelId));
        simpMessagingTemplate.convertAndSend(destination + "server/" + serverId, notification);
    }

    private List<Long> getAllChannelsInServer(Long userId, Long serverId) {
        List<Long> channelIds = new ArrayList<>();
        List<Connection> connections = connectionService.getConnectionsByServerId(serverId);
        for (Connection connection : connections) {
            connection.getMembers().stream()
                    .filter(x -> x.getUserId().equals(userId)).findFirst()
                    .ifPresent(m -> {
                        int unreadCount = messageRepository.countUnreadMessage(connection.getChannelId(), m.getLastReadAt());
                        if (unreadCount > 0) {
                            channelIds.add(connection.getChannelId());
                        }
                    });
        }
        return channelIds;
    }

}
