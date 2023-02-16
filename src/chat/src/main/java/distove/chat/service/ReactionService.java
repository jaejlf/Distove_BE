package distove.chat.service;

import distove.chat.dto.request.ReactionRequest;
import distove.chat.dto.response.ReactionMessageResponse;
import distove.chat.dto.response.ReactionResponse;
import distove.chat.entity.Message;
import distove.chat.entity.Reaction;
import distove.chat.exception.DistoveException;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static distove.chat.dto.response.ReactionMessageResponse.newReactionMessageResponse;
import static distove.chat.dto.response.ReactionResponse.newReactionResponse;
import static distove.chat.entity.Reaction.newReaction;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReactionService {

    private final MessageRepository messageRepository;
    private final UserClient userClient;

    @Transactional
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
            Reaction createdNewReaction = newReaction(reactionRequest.getEmoji(), List.of(userId));
            reactions.add(createdNewReaction);
        }
        message.updateReaction(reactions);
        messageRepository.save(message);

        return newReactionMessageResponse(reactionRequest.getMessageId(), getReactionResponses(reactions, userIds));
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
                .map(reaction -> newReactionResponse(
                        reaction.getEmoji(),
                        reaction.getUserIds().stream()
                                .map(userResponseMap::get)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

    }

}


