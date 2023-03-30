package distove.chat.service.impl;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Message;
import distove.chat.enumerate.MessageType;
import distove.chat.repository.MessageRepository;
import distove.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.entity.Message.newReply;
import static distove.chat.enumerate.MessageType.MessageStatus.CREATED;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatedStatusService implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message publishMessage(Long userId, Long channelId, MessageRequest request) {
        MessageType messageType = request.getType();
        MessageType.MessageStatus messageStatus = CREATED;
        String content = request.getContent();
        String parentId = request.getParentId();

        Message message;
        if (parentId != null) {
            message = messageRepository.save(
                    newReply(channelId, userId, messageType, CREATED, content, parentId));
        } else {
            message = messageRepository.save(
                    newMessage(channelId, userId, messageType, CREATED, content));
//            notificationService.publishNotification(channelId);
        }
        return message;
    }

}
