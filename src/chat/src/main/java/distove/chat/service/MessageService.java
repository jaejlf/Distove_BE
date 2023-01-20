package distove.chat.service;

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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static distove.chat.enumerate.MessageType.WELCOME;
import static distove.chat.enumerate.MessageType.canUpdate;
import static distove.chat.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final StorageService storageService;
    private final MessageRepository messageRepository;
    private final ConnectionRepository connectionRepository;
    private final UserClient userClient;

    public MessageResponse publishMessage(Long channelId, MessageRequest request) {
        Long userId = request.getUserId();
        UserResponse writer = userClient.getUser(userId);

        Message message;
        MessageType type = request.getType();
        switch (type) {
            case TEXT:
                message = createMessage(channelId, writer, request.getType(), request.getContent());
                break;
            case IMAGE:
            case FILE:
            case VIDEO:
                String uploadUrl = storageService.uploadToS3(request.getFile(), type);
                message = createMessage(channelId, writer, request.getType(), uploadUrl);
                break;
            case MODIFIED:
            case DELETED: // TODO : Reply 여부에 따른 delete 세부 로직 필요
                message = updateMessage(request, userId);
                break;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }
        return MessageResponse.of(message, writer, userId);
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

    private Message createMessage(Long channelId, UserResponse writer, MessageType type, String content) {
        Message message = new Message(
                channelId,
                writer.getId(),
                type,
                content
        );
        return messageRepository.save(message);
    }

    private Message updateMessage(MessageRequest request, Long userId) {
        Message message = messageRepository.findById(request.getId())
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        checkAuthorized(userId, message.getUserId(), request.getType());
        message.updateMessage(request.getType(), request.getContent());
        return messageRepository.save(message);
    }

    private void saveWelcomeMessage(Long userId, Long channelId) {
        Connection connection = connectionRepository.findByChannelId(channelId);
        List<Long> connectedMemberIds = connection.getConnectedMemberIds();

        UserResponse writer = userClient.getUser(userId);
        if (!connectedMemberIds.contains(userId)) {
            connectedMemberIds.add(userId);
            connection.updateConnectedMemberIds(connectedMemberIds);
            connectionRepository.save(connection);
            createMessage(channelId, writer, WELCOME, writer.getNickname());
        }
    }

    private static void checkAuthorized(Long userId, Long writerId, MessageType type) {
        if (!Objects.equals(writerId, userId)) throw new DistoveException(NO_AUTH_ERROR);
        if (!canUpdate(type)) throw new DistoveException(NO_AUTH_ERROR);
    }

}
