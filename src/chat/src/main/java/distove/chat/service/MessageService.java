package distove.chat.service;

import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.entity.Connection;
import distove.chat.entity.Message;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.enumerate.MessageType.WELCOME;

@Slf4j
@Service
public class MessageService extends PublishService {

    public MessageService(StorageService storageService, MessageRepository messageRepository, ConnectionRepository connectionRepository, UserClient userClient) {
        super(storageService, messageRepository, connectionRepository, userClient);
    }

    public PagedMessageResponse getMessagesByChannelId(Long userId, Long channelId, int page) {
        saveWelcomeMessage(userId, channelId);

        Pageable pageable = PageRequest.of(page - 1, 5); // TODO : 테스트를 용이하게 하기 위해 임의로 5로 설정 (추후 30으로 변경 예정)
        Page<Message> messagePage = messageRepository.findAllByChannelIdAndParentIdIsNull(channelId, pageable);

        int totalPage = messagePage.getTotalPages();
        List<MessageResponse> messageResponses = messagePage.getContent()
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());

        return PagedMessageResponse.of(totalPage, messageResponses);
    }

    private void saveWelcomeMessage(Long userId, Long channelId) {
        Connection connection = checkChannelExist(channelId);
        List<Long> connectedMemberIds = connection.getConnectedMemberIds();
        if (connectedMemberIds.contains(userId)) return;

        addUserToConnection(userId, connection, connectedMemberIds);
        UserResponse writer = userClient.getUser(userId);
        messageRepository.save(newMessage(channelId, userId, WELCOME, writer.getNickname()));
    }

    private void addUserToConnection(Long userId, Connection connection, List<Long> connectedMemberIds) {
        connectedMemberIds.add(userId);
        connection.updateConnectedMemberIds(connectedMemberIds);
        connectionRepository.save(connection);
    }

}
