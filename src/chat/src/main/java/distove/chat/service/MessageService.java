package distove.chat.service;

import distove.chat.client.UserClient;
import distove.chat.client.dto.UserResponse;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.dto.response.ThreadInfoResponse;
import distove.chat.dto.response.UnreadInfoResponse;
import distove.chat.entity.Connection;
import distove.chat.entity.Member;
import distove.chat.entity.Message;
import distove.chat.enumerate.ScrollDirection;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static distove.chat.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConnectionRepository connectionRepository;
    private final ChatService chatService;
    private final ConnectionService connectionService;
    private final MessageConverter messageConverter;
    private final UserClient userClient;

    @Value("${message.page.size}")
    private int pageSize;

    public PagedMessageResponse getMessagesByChannelId(Long userId, Long channelId, Integer scroll, String cursorId) {
        Connection connection = connectionService.getConnection(channelId);
        List<Member> members = connection.getMembers();
        Member member = members.stream()
                .filter(x -> x.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> {
                    Member newMember = saveMember(userId, connection, members);
                    chatService.publishWelcomeMessage(userId, channelId);
                    return newMember;
                });

//        Optional<Integer> scrollOptional = Optional.ofNullable(scroll);

//        if (scroll == null) notificationService.publishAllNotification(userId, connection.getServerId()); // 안읽메 알림 PUSH

        List<Message> messages = getMessagesByCursor(channelId, scroll, cursorId);
        Map<String, String> cursorIdInfo = getCursorIdInfo(channelId, messages);
        //        Collections.reverse(messageResponses); -> 메시지 뒤지버야햄
        return PagedMessageResponse.ofDefault(
                getUnreadInfo(channelId, member),
                messageConverter.getMessageResponses(userId, messages),
                cursorIdInfo);
    }

    public List<MessageResponse> getParentByChannelId(Long userId, Long channelId) {
        connectionService.validateChannel(channelId);
        return null;
//        return messageRepository.findAllByChannelIdAndReplyNameIsNotNull(channelId)
//                .stream()
//                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId, getReplyInfo(x), x.getReactions() != null ?
//                        getReactions(x.getReactions(), Optional.empty()) : null))
//                .collect(Collectors.toList());
    }

    public PagedMessageResponse getRepliesByParentId(Long userId, String parentId) {
        List<Message> messages = messageRepository.findAllRepliesByParentId(parentId);
        ThreadInfoResponse replyInfo = getReplyInfo(getMessage(parentId));
        return PagedMessageResponse.ofChild(replyInfo, messageConverter.getMessageResponses(userId, messages));
    }

    public void unsubscribeChannel(Long userId, Long channelId) {
        Connection connection = connectionService.getConnection(channelId);
        List<Member> members = connection.getMembers();
        Member member = members.stream()
                .filter(x -> x.getUserId().equals(userId)).findFirst()
                .orElseThrow(() -> new DistoveException(USER_NOT_FOUND_ERROR));

        if (getUnreadInfo(channelId, member) == null) updateLastReadAt(userId, connection, members);
    }

    public void readAllUnreadMessages(Long userId, Long channelId) {
        Connection connection = connectionService.getConnection(channelId);
        List<Member> members = connection.getMembers();
        updateLastReadAt(userId, connection, members);
    }

    private ThreadInfoResponse getReplyInfo(Message message) {
        Long stUserId = message.getThreadStarterId();
        UserResponse stUser = userClient.getUser(stUserId);
        return null;
//        return ThreadInfoResponse.of(
//                message.getThreadName(),
//                stUser.getId(),
//                stUser.getNickname(),
//                stUser.getProfileImgUrl()
//        );
    }

    private Message getMessage(String messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));
    }

    private Member saveMember(Long userId, Connection connection, List<Member> members) {
        Member member = new Member(userId);
        members.add(member);
        connection.updateMembers(members);
        connectionRepository.save(connection);
        return member;
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

    private void updateLastReadAt(Long userId, Connection connection, List<Member> members) {
        members.replaceAll(x -> Objects.equals(x.getUserId(), userId) ? new Member(userId) : x);
        connection.updateMembers(members);
        connectionRepository.save(connection);
    }

    private List<Message> getMessagesByCursor(Long channelId, Integer scroll, String cursorId) {
        ScrollDirection scrollDirection = ScrollDirection.getScrollDirection(scroll);
        LocalDateTime createdAt = getMessage(cursorId).getCreatedAt();

        List<Message> messages = new ArrayList<>();
        switch (scrollDirection) {
            case DEFAULT:
                messages = messageRepository.findAllParentByChannelId(channelId, pageSize);
                break;
            case DOWN:
                messages = messageRepository.findAllParentByChannelIdPrevious(channelId, createdAt, pageSize);
                break;
            case UP:
                messages = messageRepository.findAllParentByChannelIdNext(channelId, createdAt, pageSize);
                break;
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

}
