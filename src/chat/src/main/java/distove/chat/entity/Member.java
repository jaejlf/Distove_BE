package distove.chat.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Member {

    private final Long userId;
    private final LocalDateTime lastReadAt;

    public Member(Long userId) {
        this.userId = userId;
        this.lastReadAt = LocalDateTime.now();
    }

}
