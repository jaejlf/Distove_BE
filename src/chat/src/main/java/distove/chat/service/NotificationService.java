package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.entity.Member;
import distove.chat.entity.Message;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static distove.chat.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConnectionRepository connectionRepository;
    private final MessageRepository messageRepository;

    public void publishAllNotification(Long userId, Long serverId) {
        List<Long> channelIds = new ArrayList<>();
        List<Connection> connections = connectionRepository.findAllByServerId(serverId);
        for (Connection connection : connections) {
            List<Member> members = connection.getMembers();
            Member member = members.stream()
                    .filter(x -> x.getUserId().equals(userId)).findFirst()
                    .orElseThrow(() -> new DistoveException(USER_NOT_FOUND));

            List<Message> unreadMessages = messageRepository.findUnreadMessage(connection.getChannelId(), member.getLatestConnectedAt());
            if (unreadMessages.size() > 0) channelIds.add(connection.getChannelId());
        }

        Map<String, List<Long>> map = new HashMap<>();
        map.put("channelIds", channelIds);
        simpMessagingTemplate.convertAndSend("/sub/server/" + serverId, map);
    }

    public void publishNotification(Long channelId) {
        Long serverId = connectionRepository.findByChannelId(channelId).get().getServerId();
        Map<String, Long> map = new HashMap<>();
        map.put("channelId", channelId);
        simpMessagingTemplate.convertAndSend("/sub/server/" + serverId, map);
    }

}
