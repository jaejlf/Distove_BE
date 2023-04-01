package distove.chat.service.impl;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Message;
import distove.chat.exception.DistoveException;
import distove.chat.repository.MessageRepository;
import distove.chat.service.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static distove.chat.entity.Message.*;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
public class ReactMessageGenerator implements MessageGenerator {

    private final MessageRepository messageRepository;

    /**
     * 메시지 반응하기
     */
    @Override
    public Message createMessage(Long userId, Long channelId, MessageRequest request) {
        String messageId = request.getMessageId();
        String emoji = request.getEmoji();

        Message message = messageRepository.findByIdAndChannelId(messageId, channelId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));
        List<Reaction> reactions = message.getReactions();
        updateReactions(userId, emoji, message, reactions);
        return message;
    }

    private void updateReactions(Long userId, String emoji, Message message, List<Reaction> reactions) {
        reactions.stream().filter(r -> r.getEmoji().equals(emoji)).findFirst().ifPresentOrElse(
                reaction -> {
                    if (reaction.getUserIds().removeIf(id -> Objects.equals(id, userId))) {
                        if (reaction.getUserIds().isEmpty()) {
                            reactions.remove(reaction);
                        }
                    } else {
                        reaction.getUserIds().add(userId);
                    }
                },
                () -> reactions.add(new Reaction(emoji, List.of(userId)))
        );
        message.updateReaction(reactions);
        messageRepository.save(message);
    }

}
