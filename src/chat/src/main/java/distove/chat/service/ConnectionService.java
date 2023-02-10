package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.entity.Member;
import distove.chat.enumerate.MessageType.MessageStatus;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static distove.chat.entity.Connection.newConnection;
import static distove.chat.entity.Member.*;
import static distove.chat.entity.Message.newMessage;
import static distove.chat.enumerate.MessageType.WELCOME;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConnectionService {

    private final UserClient userClient;
    private final ConnectionRepository connectionRepository;
    private final MessageRepository messageRepository;

    public void createConnection(Long userId, Long serverId, Long channelId) {
        List<Member> members = new ArrayList<>();
        members.add(newMember(userId));

        if (connectionRepository.findByChannelId(channelId).isPresent()) return;

        Connection connection = newConnection(serverId, channelId, members);
        connectionRepository.save(connection);

        UserResponse writer = userClient.getUser(userId);
        messageRepository.save(newMessage(channelId, userId, WELCOME, MessageStatus.CREATED, writer.getNickname()));
    }

    public void clear(Long channelId) {
        connectionRepository.deleteAllByChannelId(channelId);
    }

    public void clearAll(List<Long> channelIds) {
        for (Long channelId : channelIds) {
            connectionRepository.deleteAllByChannelId(channelId);
        }
    }

}