package distove.chat.service;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.dto.response.ReplyInfoResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.entity.Message.newReply;
import static distove.chat.enumerate.MessageType.MessageStatus.*;
import static distove.chat.enumerate.MessageType.*;
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
        checkStatusCanChanged(request.getType(), request.getStatus());

        Message message;
        switch (request.getStatus()) {
            case CREATED:
                message = createMessage(channelId, request.getParentId(), request.getType(), request.getContent(), userId);
                break;
            case MODIFIED:
                message = modifyMessage(request.getMessageId(), request.getContent(), userId);
                break;
            case DELETED:
                message = deleteMessage(request.getMessageId(), userId);
                messageRepository.deleteById(message.getId());
                break;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.ofDefault(message, writer, userId);
    }

    public MessageResponse publishFile(Long userId, Long channelId, MessageType type, FileUploadRequest request) {
        String fileUploadUrl = storageService.uploadToS3(request.getFile(), type);
        Message message = createMessage(
                channelId,
                request.getParentId(),
                type,
                fileUploadUrl,
                userId);

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.ofDefault(message, writer, userId);
    }

    public TypedUserResponse publishTypedUser(Long userId) {
        UserResponse typedUser = userClient.getUser(userId);
        return TypedUserResponse.of(typedUser.getNickname());
    }

    public PagedMessageResponse getMessagesByChannelId(Long userId, Long channelId, int page) {
        saveWelcomeMessage(userId, channelId);

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Message> messagePage = messageRepository.findAllByChannelIdAndParentIdIsNullOrderByCreatedAtDesc(channelId, pageable);

        int totalPage = messagePage.getTotalPages();
        List<MessageResponse> messageResponses = new ArrayList<>();
        for (Message message : messagePage.getContent()) {
            UserResponse writer = userClient.getUser(message.getUserId());
            if (message.getReplyName() == null) {
                messageResponses.add(MessageResponse.ofDefault(message, writer, userId));
            } else {
                messageResponses.add(MessageResponse.ofParent(message, writer, userId, getReplyInfo(message)));
            }
        }
        Collections.reverse(messageResponses);
        return PagedMessageResponse.ofDefault(totalPage, messageResponses);
    }

    public MessageResponse createReply(Long userId, MessageRequest request) {
        Message parent = getMessage(request.getParentId());
        parent.addReplyInfo(request.getReplyName(), userId);
        messageRepository.save(parent);

        UserResponse writer = userClient.getUser(userId);
        ReplyInfoResponse replyInfoResponse = ReplyInfoResponse.of(
                request.getReplyName(),
                writer.getId(),
                writer.getNickname(),
                writer.getProfileImgUrl()
        );
        return MessageResponse.ofParent(parent, writer, userId, replyInfoResponse);
    }

    public List<MessageResponse> getParentByChannelId(Long userId, Long channelId) {
        checkChannelExist(channelId);
        return messageRepository.findAllByChannelIdAndReplyNameIsNotNull(channelId)
                .stream()
                .map(x -> MessageResponse.ofParent(x, userClient.getUser(x.getUserId()), userId, getReplyInfo(x)))
                .collect(Collectors.toList());
    }

    public PagedMessageResponse getChildrenByParentId(Long userId, String parentId, int page) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Message> replyPage = messageRepository.findAllByParentIdOrderByCreatedAtDesc(parentId, pageable);

        int totalPage = replyPage.getTotalPages();
        List<MessageResponse> messageResponses = replyPage.getContent()
                .stream()
                .map(x -> MessageResponse.ofDefault(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());

        Collections.reverse(messageResponses);

        ReplyInfoResponse replyInfo = getReplyInfo(getMessage(parentId));
        return PagedMessageResponse.ofChild(totalPage, replyInfo, messageResponses);
    }

    public void clear(Long channelId) {
        messageRepository.deleteAllByChannelId(channelId);
    }

    private Message createMessage(Long channelId, String parentId, MessageType type, String content, Long userId) {
        Message message;
        if (parentId != null) {
            message = messageRepository.save(
                    newReply(channelId, userId, type, CREATED, content, parentId));
        } else {
            message = messageRepository.save(
                    newMessage(channelId, userId, type, CREATED, content));
        }
        return message;
    }

    private Message modifyMessage(String messageId, String content, Long userId) {
        Message origin = getMessage(messageId);
        checkAuthorization(userId, origin);
        origin.updateMessage(MODIFIED, content);
        return messageRepository.save(origin);
    }

    private Message deleteMessage(String messageId, Long userId) {
        Message origin = getMessage(messageId);
        checkAuthorization(userId, origin);
        deleteAssociatedData(origin);
        origin.updateMessage(DELETED, "삭제된 메시지입니다");
        return origin;
    }

    private ReplyInfoResponse getReplyInfo(Message message) {
        Long stUserId = message.getStUserId();
        UserResponse stUser = userClient.getUser(stUserId);
        return ReplyInfoResponse.of(
                message.getReplyName(),
                stUser.getId(),
                stUser.getNickname(),
                stUser.getProfileImgUrl()
        );
    }

    private Message getMessage(String messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));
    }

    private static void checkAuthorization(Long userId, Message message) {
        if (!Objects.equals(message.getUserId(), userId)) throw new DistoveException(NO_AUTH);
    }

    private void deleteAssociatedData(Message origin) {
        if (isFileType(origin.getType())) storageService.deleteFile(origin.getContent());
        if (origin.getReplyName() != null) deleteReplies(origin);
    }

    private void deleteReplies(Message message) {
        List<Message> replies = messageRepository.findAllByParentId(message.getId());
        for (Message reply : replies) {
            if (isFileType(reply.getType())) storageService.deleteFile(message.getContent());
        }
        messageRepository.deleteAllByParentId(message.getId());
    }

    private void saveWelcomeMessage(Long userId, Long channelId) {
        Connection connection = checkChannelExist(channelId);
        List<Long> connectedMemberIds = connection.getConnectedMemberIds();
        if (connectedMemberIds.contains(userId)) return;

        addUserToConnection(userId, connection, connectedMemberIds);
        UserResponse writer = userClient.getUser(userId);
        messageRepository.save(newMessage(channelId, userId, WELCOME, CREATED, writer.getNickname()));
    }

    private void addUserToConnection(Long userId, Connection connection, List<Long> connectedMemberIds) {
        connectedMemberIds.add(userId);
        connection.updateConnectedMemberIds(connectedMemberIds);
        connectionRepository.save(connection);
    }

    private Connection checkChannelExist(Long channelId) {
        return connectionRepository.findByChannelId(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND));
    }

    public void clearAll(List<Long> channelIds) {
        for (Long channelId : channelIds) {
            messageRepository.deleteAllByChannelId(channelId);
        }
    }

}
