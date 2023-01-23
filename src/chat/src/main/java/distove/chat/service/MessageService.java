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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static distove.chat.entity.Message.*;
import static distove.chat.enumerate.MessageType.WELCOME;
import static distove.chat.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StorageService storageService;
    private final MessageRepository messageRepository;
    private final ConnectionRepository connectionRepository;
    private final UserClient userClient;

    public MessageResponse publishMessage(Long channelId, MessageRequest request) {
        Long userId = request.getUserId();

        Message message;
        MessageType type = request.getType();

        switch (type) {
            case TEXT:
                message = createMessage(channelId, userId, type, request.getContent());
                break;
            case MODIFIED:
            case DELETED: // TODO : Reply 여부에 따른 delete 세부 로직 필요
                message = updateMessage(request.getId(), userId, type,
                        request.getContent() != null ? request.getContent() : "삭제된 메시지입니다.");
                break;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(message, writer, userId);
    }

    public void publishFile(Long channelId, MessageType type, FileUploadRequest request) {
        Long userId = request.getUserId();
        String fileUploadUrl = storageService.uploadToS3(request.getFile(), type);
        Message message = createMessage(channelId, userId, type, fileUploadUrl);

        UserResponse writer = userClient.getUser(request.getUserId());
        simpMessagingTemplate.convertAndSend(
                "/sub/" + channelId,
                MessageResponse.of(message, writer, request.getUserId())
        );
    }

    public TypedUserResponse beingTyped(Long userId) {
        UserResponse typedUser = userClient.getUser(userId);
        return TypedUserResponse.of(typedUser.getNickname());
    }

    public List<MessageResponse> getMessages(Long userId, Long channelId) {
        saveWelcomeMessage(userId, channelId);
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());
    }

    private Message createMessage(Long channelId, Long userId, MessageType type, String content) {
        Message message = newMessage(channelId, userId, type, content);
        return messageRepository.save(message);
    }

    private Message updateMessage(String messageId, Long userId, MessageType type, String content) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        if (!Objects.equals(message.getUserId(), userId)) throw new DistoveException(NO_AUTH_ERROR);
        message.updateMessage(type, content);
        return messageRepository.save(message);
    }

    private void saveWelcomeMessage(Long userId, Long channelId) {
        Connection connection = connectionRepository.findByChannelId(channelId);
        List<Long> connectedMemberIds = connection.getConnectedMemberIds();

        if (connectedMemberIds.contains(userId)) return;

        addUserToConnection(userId, connection, connectedMemberIds);
        UserResponse writer = userClient.getUser(userId);
        createMessage(channelId, userId, WELCOME, writer.getNickname());
    }

    private void addUserToConnection(Long userId, Connection connection, List<Long> connectedMemberIds) {
        connectedMemberIds.add(userId);
        connection.updateConnectedMemberIds(connectedMemberIds);
        connectionRepository.save(connection);
    }

}
