package distove.chat.util;

import distove.chat.client.UserClient;
import distove.chat.client.dto.UserResponse;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ThreadInfoResponse;
import distove.chat.entity.Message;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static distove.chat.dto.response.MessageResponse.ReactionResponse;
import static distove.chat.entity.Message.*;

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
        List<MessageResponse> messageResponses = messages.stream()
                .map(message -> getMessageResponse(userId, message))
                .collect(Collectors.toList());
        Collections.reverse(messageResponses);
        return messageResponses;
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
