package distove.chat.service;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.entity.Message;
import distove.chat.enumerate.MessageType;
import distove.chat.exception.DistoveException;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.exception.ErrorCode.*;

@RequiredArgsConstructor
public abstract class PublishService {

    protected final StorageService storageService;
    protected final MessageRepository messageRepository;
    protected final UserClient userClient;

    protected abstract MessageResponse publishMessage(Long channelId, MessageRequest request);
    protected abstract MessageResponse publishFile(Long channelId, MessageType type, FileUploadRequest request);

    protected Message createMessageByType(Long channelId, MessageRequest request, Long userId) {
        Message message;
        MessageType type = request.getType();

        switch (type) {
            case TEXT:
                message = newMessage(channelId, userId, type, request.getContent());
                break;
            case MODIFIED:
            case DELETED: // TODO : Reply 여부에 따른 delete 세부 로직 필요
                message = updateMessage(request.getMessageId(), userId, type,
                        request.getContent() != null ? request.getContent() : "삭제된 메시지입니다.");
                break;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }
        return message;
    }

    protected Message updateMessage(String messageId, Long userId, MessageType type, String content) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        if (!Objects.equals(message.getUserId(), userId)) throw new DistoveException(NO_AUTH_ERROR);

        message.updateMessage(type, content);
        return message;
    }

}
