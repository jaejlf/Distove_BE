package distove.chat.service;

import distove.chat.client.UserClient;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.dto.response.ThreadInfoResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static distove.chat.dto.response.PagedMessageResponse.CursorInfoResponse;
import static distove.chat.dto.response.PagedMessageResponse.UnreadInfoResponse;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND;
import static distove.chat.exception.ErrorCode.USER_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConnectionRepository connectionRepository;
    private final ConnectionService connectionService;
    private final MessageConverter messageConverter;
    private final UserClient userClient;

    @Value("${message.page.size}")
    private int pageSize;

    public PagedMessageResponse getMessagesByChannelId(Long userId, Long channelId, Integer scroll, String cursorId) {
        Member member = connectionService.getOrCreateMember(userId, channelId);
        List<Message> messages = getMessagesByCursor(channelId, scroll, cursorId);

        List<MessageResponse> messageResponses = messageConverter.getMessageResponses(userId, messages);
        UnreadInfoResponse unread = getUnreadInfo(channelId, member);
        CursorInfoResponse cursorInfo = getCursorInfo(channelId, messages);

        return PagedMessageResponse.ofDefault(messageResponses, unread, cursorInfo);
    }

    public PagedMessageResponse getThreadsByMessageId(Long userId, String messageId) {
        List<Message> messages = messageRepository.findAllThreadsByParentId(messageId);
        List<MessageResponse> messageResponses = messageConverter.getMessageResponses(userId, messages);
        ThreadInfoResponse threadInfo = getThreadInfo(getMessage(messageId));
        return PagedMessageResponse.ofChild(messageResponses, threadInfo);
    }

    public List<MessageResponse> getThreadsByChannelId(Long userId, Long channelId) {
        List<Message> messages = messageRepository.findAllByChannelIdAndThreadNameIsNotNull(channelId);
        return messageConverter.getMessageResponses(userId, messages);
    }

    private UnreadInfoResponse getUnreadInfo(Long channelId, Member member) {
        LocalDateTime lastReadAt = member.getLastReadAt();

        int unreadCount = messageRepository.countUnreadMessage(channelId, lastReadAt);
        if (unreadCount > 0) {
            return UnreadInfoResponse.of(
                    lastReadAt,
                    unreadCount,
                    messageRepository.findFirstUnreadMessage(channelId, lastReadAt).getId());
        }
        return null;
    }

    private CursorInfoResponse getCursorInfo(Long channelId, List<Message> messages) {
        String previousCursorId = null;
        String nextCursorId = null;

        if (!messages.isEmpty()) {
            Optional<Message> previousCursor = messageRepository.findPreviousByCursor(channelId, messages.get(messages.size() - 1).getCreatedAt());
            Optional<Message> nextCursor = messageRepository.findNextByCursor(channelId, messages.get(0).getCreatedAt());
            if (previousCursor.isPresent()) previousCursorId = previousCursor.get().getId();
            if (nextCursor.isPresent()) nextCursorId = nextCursor.get().getId();
        }

        return CursorInfoResponse.of(previousCursorId, nextCursorId);
    }

    public Message getMessage(String messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));
    }

    /////////////////

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

    private ThreadInfoResponse getThreadInfo(Message message) {
        return ThreadInfoResponse.of(
                message.getThreadName(),
                userClient.getUser(message.getThreadStarterId()));
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


}
