package distove.chat.service;

import distove.chat.client.CommunityClient;
import distove.chat.client.UserClient;
import distove.chat.client.dto.UserResponse;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ReactionResponse;
import distove.chat.dto.response.ThreadInfoResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.entity.Message;
import distove.chat.entity.Reaction;
import distove.chat.exception.DistoveException;
import distove.chat.factory.MessageFactory;
import distove.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static distove.chat.enumerate.MessageType.validateTypeAndStatus;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND;
import static distove.chat.exception.ErrorCode.USER_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final MessageFactory messageFactory;
    private final UserClient userClient;
    private final CommunityClient communityClient;

    public MessageResponse publishMessage(Long userId, Long channelId, MessageRequest request) {
        if (!communityClient.isMember(channelId, userId)) throw new DistoveException(USER_NOT_FOUND_ERROR);
        validateTypeAndStatus(request.getType(), request.getStatus());

        MessageGenerator messageGenerator = messageFactory.getServiceByStatus(request.getStatus());
        Message message = messageGenerator.createMessage(userId, channelId, request);
        return getMessageResponse(userId, message);
    }

    public TypedUserResponse publishTypedUser(Long userId) {
        UserResponse typedUser = userClient.getUser(userId);
        return TypedUserResponse.of(typedUser.getNickname());
    }

    public MessageResponse createThread(Long userId, Long channelId, MessageRequest request) {
        Message parent = messageRepository.findByIdAndChannelId(request.getParentId(), channelId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND));

        parent.createThread(request.getThreadName(), userId);
        messageRepository.save(parent);
        return getMessageResponse(userId, parent);
    }

    private MessageResponse getMessageResponse(Long userId, Message parent) {
        UserResponse writer = userClient.getUser(userId);
        List<ReactionResponse> reactions = getReactions(parent.getReactions());
        Optional<ThreadInfoResponse> threadInfo = Optional.ofNullable(parent.getThreadName())
                .map(threadName -> ThreadInfoResponse.of(threadName, userClient.getUser(parent.getThreadStarterId())));

        return MessageResponse.of(parent, writer, userId, reactions, threadInfo);
    }

    public List<ReactionResponse> getReactions(List<Reaction> reactions) {
        Set<Long> userIds = reactions.stream()
                .flatMap(reaction -> reaction.getUserIds().stream())
                .collect(Collectors.toSet());

        List<UserResponse> users = userClient.getUsers(userIds.toString().replaceAll("[\\[\\]]", ""));
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
