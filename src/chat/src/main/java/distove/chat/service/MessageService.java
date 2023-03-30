package distove.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {
//

//    @Value("${message.page.size}")
//    private int pageSize;

//    public PagedMessageResponse getMessagesByChannelId(Long userId, Long channelId, Integer scroll, String cursorId) {
//        if (!communityClient.isMember(channelId, userId)) throw new DistoveException(USER_NOT_FOUND_ERROR);
//        Connection connection = checkChannelExist(channelId);
//        List<Member> members = connection.getMembers();
//        Member member = members.stream()
//                .filter(x -> x.getUserId().equals(userId)).findFirst()
//                .orElse(null);
//
//        if (member == null) member = saveWelcomeMessage(userId, channelId, connection, members);
//        if (scroll == null) notificationService.publishAllNotification(userId, connection.getServerId()); // 안읽메 알림 PUSH
//
//        List<Message> messages = getMessagesByCursor(channelId, scroll, cursorId);
//        Map<String, String> cursorIdInfo = getCursorIdInfo(channelId, messages);
//        return PagedMessageResponse.ofDefault(
//                getUnreadInfo(channelId, member),
//                convertMessagesToDto(userId, messages),
//                cursorIdInfo);
//    }
//
//    public List<MessageResponse> getParentByChannelId(Long userId, Long channelId) {
//        checkChannelExist(channelId);
//        return null;
////        return messageRepository.findAllByChannelIdAndReplyNameIsNotNull(channelId)
////                .stream()
////                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId, getReplyInfo(x), x.getReactions() != null ?
////                        getReactions(x.getReactions(), Optional.empty()) : null))
////                .collect(Collectors.toList());
//    }
//
//    public PagedMessageResponse getRepliesByParentId(Long userId, String parentId) {
//        List<Message> messages = messageRepository.findAllRepliesByParentId(parentId);
//        ThreadInfoResponse replyInfo = getReplyInfo(getMessage(parentId));
//        return PagedMessageResponse.ofChild(replyInfo, convertMessagesToDto(userId, messages));
//    }
//
//    public void deleteByChannelId(Long channelId) {
//        messageRepository.deleteAllByChannelId(channelId);
//    }
//
//    public void unsubscribeChannel(Long userId, Long channelId) {
//        Connection connection = checkChannelExist(channelId);
//        List<Member> members = connection.getMembers();
//        Member member = members.stream()
//                .filter(x -> x.getUserId().equals(userId)).findFirst()
//                .orElseThrow(() -> new DistoveException(USER_NOT_FOUND_ERROR));
//
//        if (getUnreadInfo(channelId, member) == null) updateLastReadAt(userId, connection, members);
//    }
//
//    public void readAllUnreadMessages(Long userId, Long channelId) {
//        Connection connection = checkChannelExist(channelId);
//        List<Member> members = connection.getMembers();
//        updateLastReadAt(userId, connection, members);
//    }
//
//    private ThreadInfoResponse getReplyInfo(Message message) {
//        Long stUserId = message.getThreadStarterId();
//        UserResponse stUser = userClient.getUser(stUserId);
//        return ThreadInfoResponse.of(
//                message.getThreadName(),
//                stUser.getId(),
//                stUser.getNickname(),
//                stUser.getProfileImgUrl()
//        );
//    }
//
//    private Message getMessage(String messageId) {
//        return messageRepository.findById(messageId)
//                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));
//    }
//
//    private Member saveWelcomeMessage(Long userId, Long channelId, Connection connection, List<Member> members) {
//        Member member;
//        UserResponse writer = userClient.getUser(userId);
//        messageRepository.save(newMessage(channelId, userId, WELCOME, writer.getNickname(), null));
//        member = addUserToConnection(userId, connection, members);
//        return member;
//    }
//
//    private Member addUserToConnection(Long userId, Connection connection, List<Member> members) {
//        Member member = new Member(userId);
//        members.add(member);
//        connection.updateMembers(members);
//        connectionRepository.save(connection);
//        return member;
//    }
//
//    private Connection checkChannelExist(Long channelId) {
//        return connectionRepository.findByChannelId(channelId)
//                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
//    }
//
//    private UnreadInfoResponse getUnreadInfo(Long channelId, Member member) {
//        UnreadInfoResponse unread = null;
//        int unreadCount = messageRepository.countUnreadMessage(channelId, member.getLastReadAt());
//        if (unreadCount > 0) {
//            unread = UnreadInfoResponse.of(
//                    member.getLastReadAt(),
//                    unreadCount,
//                    messageRepository.findFirstUnreadMessage(channelId, member.getLastReadAt()).getId());
//        }
//        return unread;
//    }
//
//    private List<MessageResponse> convertMessagesToDto(Long userId, List<Message> messages) {
//        List<MessageResponse> messageResponses = new ArrayList<>();
//        for (Message message : messages) {
//            UserResponse writer = userClient.getUser(message.getUserId());
//            List<ReactionResponse> reactions = message.getReactions() != null ? getReactions(message.getReactions()) : null;
//
//            if (message.getThreadName() == null) {
//                messageResponses.add(MessageResponse.ofDefault(message, writer, userId, reactions));
//            } else {
//                messageResponses.add(MessageResponse.ofParent(message, writer, userId, getReplyInfo(message), reactions));
//            }
//        }
//        Collections.reverse(messageResponses);
//        return messageResponses;
//    }
//
//    private void updateLastReadAt(Long userId, Connection connection, List<Member> members) {
//        members.replaceAll(x -> Objects.equals(x.getUserId(), userId) ? new Member(userId) : x);
//        connection.updateMembers(members);
//        connectionRepository.save(connection);
//    }
//
//    private List<Message> getMessagesByCursor(Long channelId, Integer scroll, String cursorId) {
//        List<Message> messages;
//
//        switch (scroll != null ? scroll : -1) {
//            case -1:
//                messages = messageRepository.findAllParentByChannelId(channelId, pageSize);
//                break;
//            case 0:
//                messages = messageRepository.findAllParentByChannelIdPrevious(channelId, getMessage(cursorId).getCreatedAt(), pageSize);
//                break;
//            case 1:
//                messages = messageRepository.findAllParentByChannelIdNext(channelId, getMessage(cursorId).getCreatedAt(), pageSize);
//                break;
//            default:
//                throw new DistoveException(SCROLL_REQUEST_ERROR);
//        }
//        return messages;
//    }
//
//    private Map<String, String> getCursorIdInfo(Long channelId, List<Message> messages) {
//        Map<String, String> cursorIdInfo = new HashMap<>();
//        String previousCursorId = null;
//        String nextCursorId = null;
//
//        if (!messages.isEmpty()) {
//            Message previousCursor = messageRepository.findPreviousByCursor(channelId, messages.get(messages.size() - 1).getCreatedAt()).orElse(null);
//            Message nextCursor = messageRepository.findNextByCursor(channelId, messages.get(0).getCreatedAt()).orElse(null);
//            previousCursorId = previousCursor != null ? previousCursor.getId() : null;
//            nextCursorId = nextCursor != null ? nextCursor.getId() : null;
//        }
//        cursorIdInfo.put("previousCursorId", previousCursorId);
//        cursorIdInfo.put("nextCursorId", nextCursorId);
//        return cursorIdInfo;
//    }
//
}
