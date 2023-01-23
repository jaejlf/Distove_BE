package distove.chat.service;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.entity.Connection;
import distove.chat.entity.Message;
import distove.chat.enumerate.MessageType;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.enumerate.MessageType.WELCOME;
import static distove.chat.exception.ErrorCode.CHANNEL_NOT_FOUND_ERROR;

@Slf4j
@Service
public class MessageService extends PublishService {

    private final ConnectionRepository connectionRepository;

    public MessageService(StorageService storageService, MessageRepository messageRepository, UserClient userClient, ConnectionRepository connectionRepository) {
        super(storageService, messageRepository, userClient);
        this.connectionRepository = connectionRepository;
    }

    @Override
    public MessageResponse publishMessage(Long channelId, MessageRequest request) {
        Long userId = request.getUserId();

        Message message = createMessageByType(channelId, request, userId);
        messageRepository.save(message);

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(message, writer, userId);
    }

    @Override
    public MessageResponse publishFile(Long channelId, MessageType type, FileUploadRequest request) {
        Long userId = request.getUserId();

        String fileUploadUrl = storageService.uploadToS3(request.getFile(), type);
        Message message = newMessage(channelId, userId, type, fileUploadUrl);
        messageRepository.save(message);

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(message, writer, userId);
    }

    public List<MessageResponse> getMessagesByChannelId(Long userId, Long channelId) {
        saveWelcomeMessage(userId, channelId);
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());
    }

    public TypedUserResponse publishTypedUser(Long userId) {
        UserResponse typedUser = userClient.getUser(userId);
        return TypedUserResponse.of(typedUser.getNickname());
    }

    private void saveWelcomeMessage(Long userId, Long channelId) {
        Connection connection = connectionRepository.findByChannelId(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
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
