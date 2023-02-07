package distove.chat.entity;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class Reaction {

    private String emoji;
    private List<Long> userIds;

    public static Reaction newReaction(String emoji, List<Long> userIds) {
        return Reaction.builder()
                .emoji(emoji)
                .userIds(userIds)
                .build();
    }

}

