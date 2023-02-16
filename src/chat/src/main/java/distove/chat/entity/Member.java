package distove.chat.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Member {

    private Long userId;
    private LocalDateTime latestConnectedAt;

    public static Member newMember(Long userId) {
        return Member.builder()
                .userId(userId)
                .latestConnectedAt(LocalDateTime.now())
                .build();
    }

}
