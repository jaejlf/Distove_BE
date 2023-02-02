package distove.chat.service;

import com.amazonaws.util.StringUtils;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.request.ReactionRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ReactionMessageResponse;
import distove.chat.dto.response.ReactionResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.entity.Emoji;
import distove.chat.entity.Message;
import distove.chat.entity.Reaction;
import distove.chat.exception.DistoveException;
import distove.chat.repository.EmojiRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static distove.chat.dto.response.ReactionMessageResponse.newReactionMessageResponse;
import static distove.chat.dto.response.ReactionResponse.newReactionResponse;
import static distove.chat.entity.Reaction.newReaction;
import static distove.chat.exception.ErrorCode.EMOJI_NOT_FOUND;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReactionService {

    private final MessageRepository messageRepository;
    private final UserClient userClient;

    @Transactional
    public ReactionMessageResponse reactMessage(ReactionRequest reactionRequest, Long userId) {

        UserResponse user = userClient.getUser(userId);

        Message message = messageRepository.findById(reactionRequest.getMessageId())
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));


        List<Reaction> reactions = message.getReactions();
        if (reactions == null) {
            reactions = new ArrayList<Reaction>();
        }

        Optional<Reaction> reaction = reactions.stream().filter(r -> r.getEmoji().equals(reactionRequest.getEmoji()))
                .findFirst();

        List<ReactionResponse> reactionResponses = new ArrayList<>();

        if (reaction.isEmpty()) {
            Reaction createdNewReaction = newReaction(reactionRequest.getEmoji(), new ArrayList<Long>() {{
                add(userId);
            }});
            reactions.add(createdNewReaction);
            reactionResponses.add(newReactionResponse(createdNewReaction.getEmoji(), new ArrayList<UserResponse>() {{
                add(user);
            }}));
        } else {
            //이미 사용됐던 emoji라면
            List<Long> userIds = reaction.get().getUserIds();
            //내가 이미 눌렀던 emoji면 삭제하고

            if (!userIds.removeIf(id -> id.equals(userId))) {
                // if not removed
                userIds.add(userId);

            }

            //다 했는데(취소) 만약 비었다면
            if (userIds.isEmpty()) {

                reactions.removeIf(r -> reaction.get().getEmoji().equals(r.getEmoji()));
            }
        }
        for (Reaction r : reactions) {
            String userIdsString = r.getUserIds().toString().replace("[","").replace("]","");
            List<UserResponse> userResponses = userClient.getUsers(userIdsString);
            reactionResponses.add(newReactionResponse(r.getEmoji(),userClient.getUsers(userIdsString)));
        }
        message.updateReaction(reactions);
        messageRepository.save(message);

        return newReactionMessageResponse(reactionRequest.getMessageId(),reactionResponses);
    }

}
