package distove.chat.service;

import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
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
import java.util.stream.Collectors;

import static distove.chat.enumerate.MessageType.*;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND_ERROR;
import static distove.chat.exception.ErrorCode.MESSAGE_TYPE_ERROR;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;
    private final ConnectionRepository connectionRepository;
    private final UserClient userClient;

    UserResponse writer = new UserResponse(0L, "더미더미", "www.xxx"); // 임시 더미 유저

    public void publishMessage(Long userId, Long channelId, MessageRequest request) {
//        UserResponse writer = userClient.getUser(userId);

        Message message = null;
        MessageType type = request.getType();
        switch (type) {
            case TEXT:
                message = createMessage(channelId, writer, request.getType(), request.getContent());
                break;
            case FILE:
            case IMAGE:
            case VIDEO:
                String uploadUrl = request.getContent(); // 스토리지에 파일 업로드하는 과정 추가 필요
                message = createMessage(channelId, writer, request.getType(), uploadUrl);
                break;
            case MODIFIED:
                message = updateMessage(request);
                break;
            case DELETED:
                break;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }

        messageRepository.save(message);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, message);
    }

    public List<MessageResponse> getMessages(Long userId, Long channelId) {
        sendWelcomeMessage(userId, channelId);
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        return messages
                .stream()
                .map(x -> MessageResponse.of(x, writer, userId))
                .collect(Collectors.toList());
//        return messages
//                .stream()
//                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
//                .collect(Collectors.toList());
    }

    private void sendWelcomeMessage(Long userId, Long channelId) {
        Connection connection = connectionRepository.findByChannelId(channelId);
        List<Long> connectedMemberIds = connection.getConnectedMemberIds();

//        UserResponse writer = userClient.getUser(userId);
        if (!connectedMemberIds.contains(userId)) {
            connectedMemberIds.add(userId);
            connection.updateConnectedMemberIds(connectedMemberIds);
            connectionRepository.save(connection);

            Message message = createMessage(channelId, writer, WELCOME, writer.getNickname());
            simpMessagingTemplate.convertAndSend("/sub/" + channelId, message);
        }
    }

    private static Message createMessage(Long channelId, UserResponse writer, MessageType type, String content) {
        return new Message(
                channelId,
                writer.getId(),
                type,
                content
        );
    }

    private Message updateMessage(MessageRequest request) {
        Message message = messageRepository.findById(request.getId())
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));
        message.updateMessage(request.getType(), request.getContent());
        return message;
    }

}
