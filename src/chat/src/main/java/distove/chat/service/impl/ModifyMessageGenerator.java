package distove.chat.service.impl;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Message;
import distove.chat.exception.DistoveException;
import distove.chat.repository.MessageRepository;
import distove.chat.service.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static distove.chat.enumerate.MessageType.MessageStatus.MODIFIED;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND;
import static distove.chat.exception.ErrorCode.NO_AUTH_ERROR;

@Service
@RequiredArgsConstructor
public class ModifyMessageGenerator implements MessageGenerator {

    private final MessageRepository messageRepository;

    @Override
    public Message createMessage(Long userId, Long channelId, MessageRequest request) {
        String messageId = request.getMessageId();
        String content = request.getContent();

        Message origin = messageRepository.findByIdAndChannelId(messageId, channelId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));
        checkAuthorization(userId, origin);
        origin.updateMessage(MODIFIED, content);
        return messageRepository.save(origin);
    }

    private static void checkAuthorization(Long userId, Message message) {
        if (!Objects.equals(message.getUserId(), userId)) throw new DistoveException(NO_AUTH_ERROR);
    }

}
