package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.entity.Member;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static distove.chat.exception.ErrorCode.CHANNEL_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConnectionRepository connectionRepository;
    private final MessageRepository messageRepository;

    @Value("${sub.destination}")
    private String destination;

    public void publishAllNotification(Long userId, Long serverId) {
        List<Long> channelIds = new ArrayList<>();
        List<Connection> connections = connectionRepository.findAllByServerId(serverId);
        for (Connection connection : connections) {
            List<Member> members = connection.getMembers();
            Member member = members.stream()
                    .filter(x -> x.getUserId().equals(userId)).findFirst()
                    .orElse(null);

            if (member != null) {
                int unreadCount = messageRepository.countUnreadMessage(connection.getChannelId(), member.getLatestConnectedAt());
                if (unreadCount > 0) channelIds.add(connection.getChannelId());
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("serverId", serverId);
        map.put("channelIds", channelIds);
        simpMessagingTemplate.convertAndSend(destination + "server/" + serverId, map);
    }

    public void publishNotification(Long channelId) {
        Long serverId = connectionRepository.findByChannelId(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND)).getServerId();

        Map<String, Object> map = new HashMap<>();
        map.put("serverId", serverId);
        map.put("channelId", channelId);
        simpMessagingTemplate.convertAndSend(destination + "server/" + serverId, map);
    }

}
