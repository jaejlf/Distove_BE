package distove.chat.entity;

import lombok.Getter;

import java.util.List;

@Getter
public class Reaction {

    private final String emoji;
    private final List<Long> userIds;

    public Reaction(String emoji, List<Long> userIds) {
        this.emoji = emoji;
        this.userIds = userIds;
    }

}
