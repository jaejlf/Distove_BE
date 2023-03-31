package distove.chat.service;

import distove.chat.client.UserClient;
import distove.chat.client.dto.UserResponse;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.TypingUserResponse;
import distove.chat.entity.Message;
import distove.chat.exception.DistoveException;
import distove.chat.factory.MessageFactory;
import distove.chat.repository.MessageRepository;
import distove.chat.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;
    private final ConnectionService connectionService;
    private final MessageFactory messageFactory;
    private final MessageConverter messageConverter;
    private final UserClient userClient;

    @Value("${sub.destination}")
    private String destination;

    public MessageResponse publishMessage(Long userId, Long channelId, MessageRequest request) {
        MessageGenerator messageGenerator = messageFactory.getServiceByStatus(request.getStatus());
        Message message = messageGenerator.createMessage(userId, channelId, request);
        return messageConverter.getMessageResponse(userId, message);
    }

    public TypingUserResponse publishTypingUser(Long userId) {
        UserResponse typingUser = userClient.getUser(userId);
        return TypingUserResponse.of(typingUser.getNickname());
    }

    public void publishWelcomeMessage(Long userId, Long channelId) {
        UserResponse user = userClient.getUser(userId);
        MessageResponse result = publishMessage(userId, channelId, MessageRequest.ofWelcome(user.getNickname()));
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    public void createThread(Long userId, Long channelId, MessageRequest request) {
        Message message = messageRepository.findByIdAndChannelId(request.getParentId(), channelId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));
        message.createThread(request.getThreadName(), userId);
        messageRepository.save(message);

        MessageResponse threadMessage = messageConverter.getMessageResponse(userId, message);
        simpMessagingTemplate.convertAndSend(destination + channelId, threadMessage);
    }

}
