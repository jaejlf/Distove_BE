package distove.chat.service.impl;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Message;
import distove.chat.enumerate.MessageType;
import distove.chat.repository.MessageRepository;
import distove.chat.service.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static distove.chat.entity.Message.newMessage;

@Service
@RequiredArgsConstructor
public class CreateMessageGenerator implements MessageGenerator {

    private final MessageRepository messageRepository;

    @Override
    public Message createMessage(Long userId, Long channelId, MessageRequest request) {
        MessageType type = request.getType();
        String content = request.getContent();
        String parentId = request.getParentId();

        Message message = messageRepository.save(newMessage(channelId, userId, type, content, parentId));
        return messageRepository.save(message);
    }

}
