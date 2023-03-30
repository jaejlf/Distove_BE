package distove.chat.service;

import distove.chat.client.CommunityClient;
import distove.chat.client.UserClient;
import distove.chat.client.dto.UserResponse;
import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.request.ReactionRequest;
import distove.chat.dto.response.*;
import distove.chat.entity.Connection;
import distove.chat.entity.Member;
import distove.chat.entity.Message;
import distove.chat.entity.Reaction;
import distove.chat.enumerate.MessageType;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static distove.chat.dto.response.ReactionMessageResponse.of;
import static distove.chat.entity.Message.newMessage;
import static distove.chat.entity.Message.newReply;
import static distove.chat.enumerate.MessageType.MessageStatus.*;
import static distove.chat.enumerate.MessageType.*;
import static distove.chat.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    @Value("${message.page.size}")
    private int pageSize;

    private final StorageService storageService;
    private final NotificationService notificationService;
    private final MessageRepository messageRepository;
    private final ConnectionRepository connectionRepository;
    private final UserClient userClient;
    private final CommunityClient communityClient;

    public MessageResponse publishMessage(Long userId, Long channelId, MessageRequest request) {
        if (!communityClient.isMember(channelId, userId)) throw new DistoveException(USER_NOT_FOUND_ERROR);
        validateTypeAndStatus(request.getType(), request.getStatus());

        Message message;
        switch (request.getStatus()) {
            case CREATED:
                message = createMessage(channelId, request.getParentId(), request.getType(), request.getContent(), userId);
                break;
            case MODIFIED:
                message = modifyMessage(request.getMessageId(), request.getContent(), userId);
                break;
            case DELETED:
                message = deleteByChannelId(request.getMessageId(), userId);
                messageRepository.deleteById(message.getId());
                break;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }

        UserResponse writer = userClient.getUser(userId);
        List<ReactionResponse> reactions = message.getReactions() != null ? getUserInfoOfReactions(message.getReactions()) : null;
        return MessageResponse.ofDefault(message, writer, userId, reactions);
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
        List<ReactionResponse> reactions = message.getReactions() != null ? getUserInfoOfReactions(message.getReactions()) : null;
        return MessageResponse.ofDefault(message, writer, userId, reactions);
    }

    public TypedUserResponse publishTypedUser(Long userId) {
        UserResponse typedUser = userClient.getUser(userId);
        return TypedUserResponse.of(typedUser.getNickname());
    }

    public PagedMessageResponse getMessagesByChannelId(Long userId, Long channelId, Integer scroll, String cursorId) {
        if (!communityClient.isMember(channelId, userId)) throw new DistoveException(USER_NOT_FOUND_ERROR);
        Connection connection = checkChannelExist(channelId);
        List<Member> members = connection.getMembers();
        Member member = members.stream()
                .filter(x -> x.getUserId().equals(userId)).findFirst()
                .orElse(null);

        if (member == null) member = saveWelcomeMessage(userId, channelId, connection, members);
        if (scroll == null) notificationService.publishAllNotification(userId, connection.getServerId()); // 안읽메 알림 PUSH

        List<Message> messages = getMessagesByCursor(channelId, scroll, cursorId);
        Map<String, String> cursorIdInfo = getCursorIdInfo(channelId, messages);
        return PagedMessageResponse.ofDefault(
                getUnreadInfo(channelId, member),
                convertMessagesToDto(userId, messages),
                cursorIdInfo);
    }

    public MessageResponse createReply(Long userId, MessageRequest request) {
        Message parent = getMessage(request.getParentId());
        parent.createReplyInfo(request.getReplyName(), userId);
        messageRepository.save(parent);


        UserResponse writer = userClient.getUser(parent.getUserId());
        UserResponse stUser = userClient.getUser(userId);
        ReplyInfoResponse replyInfoResponse = ReplyInfoResponse.of(
                request.getReplyName(),
                stUser.getId(),
                stUser.getNickname(),
                stUser.getProfileImgUrl()
        );

        List<ReactionResponse> reactions = parent.getReactions() != null ? getUserInfoOfReactions(parent.getReactions()) : null;
        return MessageResponse.ofParent(parent, writer, userId, replyInfoResponse, reactions);
    }

    public List<MessageResponse> getParentByChannelId(Long userId, Long channelId) {
        checkChannelExist(channelId);
        return messageRepository.findAllByChannelIdAndReplyNameIsNotNull(channelId)
                .stream()
                .map(x -> MessageResponse.ofParent(x, userClient.getUser(x.getUserId()), userId, getReplyInfo(x), x.getReactions() != null ?
                        getUserInfoOfReactions(x.getReactions()) : null))
                .collect(Collectors.toList());
    }

    public PagedMessageResponse getRepliesByParentId(Long userId, String parentId) {
        List<Message> messages = messageRepository.findAllRepliesByParentId(parentId);
        ReplyInfoResponse replyInfo = getReplyInfo(getMessage(parentId));
        return PagedMessageResponse.ofChild(replyInfo, convertMessagesToDto(userId, messages));
    }

    public void deleteByChannelId(Long channelId) {
        messageRepository.deleteAllByChannelId(channelId);
    }

    public void unsubscribeChannel(Long userId, Long channelId) {
        Connection connection = checkChannelExist(channelId);
        List<Member> members = connection.getMembers();
        Member member = members.stream()
                .filter(x -> x.getUserId().equals(userId)).findFirst()
                .orElseThrow(() -> new DistoveException(USER_NOT_FOUND_ERROR));

        if (getUnreadInfo(channelId, member) == null) updateLastReadAt(userId, connection, members);
    }

    public void readAllUnreadMessages(Long userId, Long channelId) {
        Connection connection = checkChannelExist(channelId);
        List<Member> members = connection.getMembers();
        updateLastReadAt(userId, connection, members);
    }

    private Message createMessage(Long channelId, String parentId, MessageType type, String content, Long userId) {
        Message message;
        if (parentId != null) {
            message = messageRepository.save(
                    newReply(channelId, userId, type, CREATED, content, parentId));
        } else {
            message = messageRepository.save(
                    newMessage(channelId, userId, type, CREATED, content));
            notificationService.publishNotification(channelId);
        }
        return message;
    }

    private Message modifyMessage(String messageId, String content, Long userId) {
        Message origin = getMessage(messageId);
        checkAuthorization(userId, origin);
        origin.updateMessage(MODIFIED, content);
        return messageRepository.save(origin);
    }

    private Message deleteByChannelId(String messageId, Long userId) {
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
        if (!Objects.equals(message.getUserId(), userId)) throw new DistoveException(NO_AUTH_ERROR);
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

    private Member saveWelcomeMessage(Long userId, Long channelId, Connection connection, List<Member> members) {
        Member member;
        UserResponse writer = userClient.getUser(userId);
        messageRepository.save(newMessage(channelId, userId, WELCOME, CREATED, writer.getNickname()));
        member = addUserToConnection(userId, connection, members);
        return member;
    }

    private Member addUserToConnection(Long userId, Connection connection, List<Member> members) {
        Member member = new Member(userId);
        members.add(member);
        connection.updateMembers(members);
        connectionRepository.save(connection);
        return member;
    }

    private Connection checkChannelExist(Long channelId) {
        return connectionRepository.findByChannelId(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
    }

    private UnreadInfoResponse getUnreadInfo(Long channelId, Member member) {
        UnreadInfoResponse unread = null;
        int unreadCount = messageRepository.countUnreadMessage(channelId, member.getLastReadAt());
        if (unreadCount > 0) {
            unread = UnreadInfoResponse.of(
                    member.getLastReadAt(),
                    unreadCount,
                    messageRepository.findFirstUnreadMessage(channelId, member.getLastReadAt()).getId());
        }
        return unread;
    }

    private List<MessageResponse> convertMessagesToDto(Long userId, List<Message> messages) {
        List<MessageResponse> messageResponses = new ArrayList<>();
        for (Message message : messages) {
            UserResponse writer = userClient.getUser(message.getUserId());
            List<ReactionResponse> reactions = message.getReactions() != null ? getUserInfoOfReactions(message.getReactions()) : null;

            if (message.getReplyName() == null) {
                messageResponses.add(MessageResponse.ofDefault(message, writer, userId, reactions));
            } else {
                messageResponses.add(MessageResponse.ofParent(message, writer, userId, getReplyInfo(message), reactions));
            }
        }
        Collections.reverse(messageResponses);
        return messageResponses;
    }

    private void updateLastReadAt(Long userId, Connection connection, List<Member> members) {
        members.replaceAll(x -> Objects.equals(x.getUserId(), userId) ? new Member(userId) : x);
        connection.updateMembers(members);
        connectionRepository.save(connection);
    }

    private List<Message> getMessagesByCursor(Long channelId, Integer scroll, String cursorId) {
        List<Message> messages;

        switch (scroll != null ? scroll : -1) {
            case -1:
                messages = messageRepository.findAllParentByChannelId(channelId, pageSize);
                break;
            case 0:
                messages = messageRepository.findAllParentByChannelIdPrevious(channelId, getMessage(cursorId).getCreatedAt(), pageSize);
                break;
            case 1:
                messages = messageRepository.findAllParentByChannelIdNext(channelId, getMessage(cursorId).getCreatedAt(), pageSize);
                break;
            default:
                throw new DistoveException(SCROLL_REQUEST_ERROR);
        }
        return messages;
    }

    private Map<String, String> getCursorIdInfo(Long channelId, List<Message> messages) {
        Map<String, String> cursorIdInfo = new HashMap<>();
        String previousCursorId = null;
        String nextCursorId = null;

        if (!messages.isEmpty()) {
            Message previousCursor = messageRepository.findPreviousByCursor(channelId, messages.get(messages.size() - 1).getCreatedAt()).orElse(null);
            Message nextCursor = messageRepository.findNextByCursor(channelId, messages.get(0).getCreatedAt()).orElse(null);
            previousCursorId = previousCursor != null ? previousCursor.getId() : null;
            nextCursorId = nextCursor != null ? nextCursor.getId() : null;
        }
        cursorIdInfo.put("previousCursorId", previousCursorId);
        cursorIdInfo.put("nextCursorId", nextCursorId);
        return cursorIdInfo;
    }

    public ReactionMessageResponse reactMessage(ReactionRequest reactionRequest, Long userId) {
        String emoji = reactionRequest.getEmoji();
        Message message = messageRepository.findById(reactionRequest.getMessageId())
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));
        List<Reaction> reactions = message.getReactions() != null ?
                message.getReactions() : new ArrayList<>();
        Set<Long> userIds = new HashSet<>(Collections.singletonList(userId));

        boolean isReacted = false;
        for (Reaction r : reactions) {
            userIds.addAll(r.getUserIds());
            if (r.getEmoji().equals(emoji)) {
                isReacted = true;
                if (r.getUserIds().removeIf(id -> id.equals(userId))) { // 만약 원래 내가 눌렀던 이모지면 삭제
                    if (r.getUserIds().isEmpty()) { // 다 지웠는데 비었다면 현재 객체 삭제하기
                        reactions.remove(r);
                        break;
                    }
                } else { // 원래 내가 눌렀던 emoji가 아니라면 내 id 추가
                    r.getUserIds().add(userId);
                }
            }
        }
        if (!isReacted) {
            Reaction createdNewReaction = new Reaction(reactionRequest.getEmoji(), List.of(userId));
            reactions.add(createdNewReaction);
        }
        message.updateReaction(reactions);
        messageRepository.save(message);

        return of(reactionRequest.getMessageId(), getReactionResponses(reactions, userIds));
    }

    public List<ReactionResponse> getUserInfoOfReactions(List<Reaction> reactions) {
        Set<Long> userIds = new HashSet<>();
        for (Reaction r : reactions) {
            userIds.addAll(r.getUserIds());
        }
        return getReactionResponses(reactions, userIds);
    }

    private List<ReactionResponse> getReactionResponses(List<Reaction> reactions, Set<Long> userIds) {
        List<UserResponse> users = userClient.getUsers(userIds.toString().replace("[", "").replace("]", ""));
        Map<Long, UserResponse> userResponseMap = users.stream().collect(Collectors.toMap(UserResponse::getId, u -> u));
        return reactions.stream()
                .map(reaction -> ReactionResponse.of(
                        reaction.getEmoji(),
                        reaction.getUserIds().stream()
                                .map(userResponseMap::get)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

    }

}
