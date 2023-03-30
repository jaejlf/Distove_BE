package distove.chat.service.impl;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Message;
import distove.chat.exception.DistoveException;
import distove.chat.repository.MessageRepository;
import distove.chat.service.MessageGenerator;
import distove.chat.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static distove.chat.enumerate.MessageType.MessageStatus;
import static distove.chat.enumerate.MessageType.isFileType;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND;
import static distove.chat.exception.ErrorCode.NO_AUTH_ERROR;

@Service
@RequiredArgsConstructor
public class DeleteMessageGenerator implements MessageGenerator {

    private final MessageRepository messageRepository;
    private final StorageService storageService;

    @Override
    public Message createMessage(Long userId, Long channelId, MessageRequest request) {
        String messageId = request.getMessageId();

        Message origin = messageRepository.findByIdAndChannelId(messageId, channelId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));
        checkAuthorization(userId, origin);

        // 메시지 및 연관 데이터 삭제
        origin.updateMessage(MessageStatus.DELETED, "삭제된 메시지입니다");
        if (isFileType(origin.getType())) storageService.deleteFile(origin.getContent());
        if (origin.getThreadName() != null) deleteReplies(origin);
        messageRepository.deleteById(messageId);

        return origin;
    }

    private static void checkAuthorization(Long userId, Message message) {
        if (!Objects.equals(message.getUserId(), userId)) throw new DistoveException(NO_AUTH_ERROR);
    }

    private void deleteReplies(Message message) {
        List<Message> replies = messageRepository.findAllByParentId(message.getId());
        for (Message reply : replies) {
            if (isFileType(reply.getType())) storageService.deleteFile(message.getContent());
        }
        messageRepository.deleteAllByParentId(message.getId());
    }

}
