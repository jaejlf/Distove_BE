package distove.chat.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Member {

    private Long userId;
    private LocalDateTime lastReadAt;

    public static Member newMember(Long userId) {
        return Member.builder()
                .userId(userId)
                .lastReadAt(LocalDateTime.now())
                .build();
    }

}
