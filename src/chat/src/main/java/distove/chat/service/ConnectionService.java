package distove.chat.service;

import distove.chat.entity.Connection;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static distove.chat.entity.Connection.*;
import static distove.chat.entity.Message.newMessage;
import static distove.chat.enumerate.MessageType.WELCOME;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConnectionService {

    private final UserClient userClient;
    private final ConnectionRepository connectionRepository;
    private final MessageRepository messageRepository;

    public void createConnection(Long channelId, Long userId) {
        List<Long> connectedMemberIds = new ArrayList<>();
        connectedMemberIds.add(userId);

        Connection connection = newConnection(channelId,connectedMemberIds);
        connectionRepository.save(connection);

        UserResponse writer = userClient.getUser(userId);
        messageRepository.save(newMessage(channelId, userId, WELCOME, writer.getNickname()));
    }

    public void clearAll(Long channelId) {
        connectionRepository.deleteAllByChannelId(channelId);
    }

}