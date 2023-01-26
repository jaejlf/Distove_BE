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

import java.util.List;
import java.util.Objects;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.entity.Message.newReply;
import static distove.chat.enumerate.MessageType.isFileType;
import static distove.chat.exception.ErrorCode.*;

@RequiredArgsConstructor
public class PublishService {

    protected final StorageService storageService;
    protected final MessageRepository messageRepository;
    protected final ConnectionRepository connectionRepository;
    protected final UserClient userClient;

    public MessageResponse publishMessage(Long userId, Long channelId, MessageRequest request) {
        checkChannelExist(channelId);
        Message message = createMessageByType(channelId, request, userId);
        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(message, writer, userId);
    }

    public MessageResponse publishFile(Long userId, Long channelId, MessageType type, FileUploadRequest request) {
        checkChannelExist(channelId);
        String fileUploadUrl = storageService.uploadToS3(request.getFile(), type);
        MessageRequest messageRequest = new MessageRequest(
                type,
                null,
                fileUploadUrl,
                request.getParentId()
        );

        Message message = createMessageByType(channelId, messageRequest, userId);
        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(message, writer, userId);
    }

    public TypedUserResponse publishTypedUser(Long userId) {
        UserResponse typedUser = userClient.getUser(userId);
        return TypedUserResponse.of(typedUser.getNickname());
    }

    protected Connection checkChannelExist(Long channelId) {
        return connectionRepository.findByChannelId(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
    }

    private Message createMessageByType(Long channelId, MessageRequest request, Long userId) {
        Message message;
        MessageType type = request.getType();

        switch (type) {
            case TEXT:
            case FILE:
            case IMAGE:
            case VIDEO:
                if (request.getParentId() != null) {
                    message = messageRepository.save(
                            newReply(channelId, userId, type, request.getContent(), request.getParentId()));
                } else {
                    message = messageRepository.save(
                            newMessage(channelId, userId, type, request.getContent()));
                }
                break;
            case MODIFIED:
                message = messageRepository.save(
                        checkAuthAndGetMessage(request.getMessageId(), userId, type, request.getContent()));
                break;
            case DELETED:
                message = checkAuthAndGetMessage(request.getMessageId(), userId, type, request.getContent());
                if (isFileType(message.getType())) storageService.deleteFile(message.getContent());
                if (message.getReplyInfo() != null) deleteReplies(message);
                messageRepository.deleteById(message.getId());
                break;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }

        return message;
    }

    private Message checkAuthAndGetMessage(String messageId, Long userId, MessageType type, String content) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        if (!Objects.equals(message.getUserId(), userId)) throw new DistoveException(NO_AUTH_ERROR);

        message.updateMessage(type, content);
        return message;
    }

    private void deleteReplies(Message message) {
        List<Message> replies = messageRepository.findAllByParentId(message.getId());
        for (Message reply : replies) {
            if (isFileType(reply.getType())) storageService.deleteFile(message.getContent());
        }
        messageRepository.deleteAllByParentId(message.getId());
    }

}
