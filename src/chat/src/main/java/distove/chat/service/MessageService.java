package distove.chat.service;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.entity.Connection;
import distove.chat.entity.Message;
import distove.chat.entity.ReplyInfo;
import distove.chat.enumerate.MessageType;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.entity.Message.newReply;
import static distove.chat.entity.ReplyInfo.newReplyInfo;
import static distove.chat.enumerate.MessageType.WELCOME;
import static distove.chat.enumerate.MessageType.isFileType;
import static distove.chat.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    @Value("${message.page.size}")
    private int pageSize;

    private final StorageService storageService;
    private final MessageRepository messageRepository;
    private final ConnectionRepository connectionRepository;
    private final UserClient userClient;

    public MessageResponse publishMessage(Long userId, Long channelId, MessageRequest request) {
        Message message = createMessageByType(channelId, request, userId);
        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(message, writer, userId);
    }

    public MessageResponse publishFile(Long userId, Long channelId, MessageType type, FileUploadRequest request) {
        String fileUploadUrl = storageService.uploadToS3(request.getFile(), type);
        MessageRequest messageRequest = new MessageRequest(
                type,
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

    public PagedMessageResponse getMessagesByChannelId(Long userId, Long channelId, int page) {
        saveWelcomeMessage(userId, channelId);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Message> messagePage = messageRepository.findAllByChannelIdAndParentIdIsNull(channelId, pageable);

        int totalPage = messagePage.getTotalPages();
        List<MessageResponse> messageResponses = messagePage.getContent()
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());

        return PagedMessageResponse.of(totalPage, messageResponses);
    }

    public MessageResponse createReply(Long userId, MessageRequest request) {
        Message parent = getMessage(request.getParentId());
        parent.addReplyInfo(newReplyInfo(request.getReplyName(), userId));
        messageRepository.save(parent);

        UserResponse writer = userClient.getUser(userId);
        ReplyInfo replyInfoDetail = ReplyInfo.withUserDetails(
                request.getReplyName(),
                writer.getId(),
                writer.getNickname(),
                writer.getProfileImgUrl()
        );
        return MessageResponse.withReplyInfo(parent, writer, userId, replyInfoDetail);
    }

    public List<MessageResponse> getParentByChannelId(Long userId, Long channelId) {
        return messageRepository.findAllByChannelIdAndReplyInfoIsNotNull(channelId)
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());
    }

    public PagedMessageResponse getChildrenByParentId(Long userId, String parentId, int page) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Message> replyPage = messageRepository.findAllByParentId(parentId, pageable);

        int totalPage = replyPage.getTotalPages();
        List<MessageResponse> messageResponses = replyPage.getContent()
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());

        return PagedMessageResponse.of(totalPage, messageResponses);
    }

    private Message createMessageByType(Long channelId, MessageRequest request, Long userId) {
        Message origin, message;
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
                origin = getMessage(request.getMessageId());
                checkAuthorization(userId, origin);
                origin.updateMessage(type, request.getContent());
                message = messageRepository.save(origin);
                break;
            case DELETED:
                origin = getMessage(request.getMessageId());
                checkAuthorization(userId, origin);
                deleteAssociatedData(origin);
                origin.updateMessage(type, "삭제된 메시지입니다");
                message = origin;
                messageRepository.deleteById(origin.getId());
                break;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }

        return message;
    }

    private Message getMessage(String messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));
    }

    private static void checkAuthorization(Long userId, Message message) {
        if (!Objects.equals(message.getUserId(), userId)) throw new DistoveException(NO_AUTH_ERROR);
    }

    private void deleteAssociatedData(Message origin) {
        if (isFileType(origin.getType())) storageService.deleteFile(origin.getContent());
        if (origin.getReplyInfo() != null) deleteReplies(origin);
    }

    private void deleteReplies(Message message) {
        List<Message> replies = messageRepository.findAllByParentId(message.getId());
        for (Message reply : replies) {
            if (isFileType(reply.getType())) storageService.deleteFile(message.getContent());
        }
        messageRepository.deleteAllByParentId(message.getId());
    }

    private void saveWelcomeMessage(Long userId, Long channelId) {
        Connection connection = connectionRepository.findByChannelId(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
        List<Long> connectedMemberIds = connection.getConnectedMemberIds();
        if (connectedMemberIds.contains(userId)) return;

        addUserToConnection(userId, connection, connectedMemberIds);
        UserResponse writer = userClient.getUser(userId);
        messageRepository.save(newMessage(channelId, userId, WELCOME, writer.getNickname()));
    }

    private void addUserToConnection(Long userId, Connection connection, List<Long> connectedMemberIds) {
        connectedMemberIds.add(userId);
        connection.updateConnectedMemberIds(connectedMemberIds);
        connectionRepository.save(connection);
    }

}
