package distove.chat.service;

import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.entity.Message;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;
    private final UserClient userClient;

    public void publishMessage(Long userId, Long channelId, MessageRequest request) throws ExecutionException, InterruptedException {
//        UserResponse writer = userClient.getUser(userId);
        UserResponse writer = new UserResponse(userId, "더미더미", "www.xxx");

        Message message;
        if (request.getId() == null) {
            message = new Message(
                    writer.getId(),
                    channelId,
                    request.getType(),
                    request.getContent()
            );
        } else {
            message = messageRepository.findById(request.getId()).get(); // Optional 예외처리 전
            message.updateMessage(request.getType(), request.getContent());
        }

        messageRepository.save(message);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, message);
    }

    public List<MessageResponse> getMessages(Long userId, Long channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        UserResponse writer = new UserResponse(userId, "더미더미", "www.xxx");
        return messages
                .stream()
                .map(x -> MessageResponse.of(x, writer, userId))
                .collect(Collectors.toList());
//        return messages
//                .stream()
//                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
//                .collect(Collectors.toList());
    }

}
