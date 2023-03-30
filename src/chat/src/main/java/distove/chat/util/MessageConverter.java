package distove.chat.util;

import distove.chat.client.UserClient;
import distove.chat.client.dto.UserResponse;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ReactionResponse;
import distove.chat.dto.response.ThreadInfoResponse;
import distove.chat.entity.Message;
import distove.chat.entity.Reaction;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageConverter {

    private final UserClient userClient;

    public MessageResponse getMessageResponse(Long userId, Message message) {
        UserResponse writer = userClient.getUser(message.getUserId());
        List<ReactionResponse> reactions = getReactions(message.getReactions());
        Optional<ThreadInfoResponse> threadInfo = Optional.ofNullable(message.getThreadName()).map(threadName ->
                ThreadInfoResponse.of(threadName, userClient.getUser(message.getThreadStarterId())));

        return MessageResponse.of(message, writer, userId, reactions, threadInfo);
    }

    public List<MessageResponse> getMessageResponses(Long userId, List<Message> messages) {
        return messages.stream()
                .map(message -> getMessageResponse(userId, message))
                .collect(Collectors.toList());
    }

    private List<ReactionResponse> getReactions(List<Reaction> reactions) {
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
