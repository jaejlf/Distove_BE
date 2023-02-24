package distove.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inviteCode;

    @ManyToOne
    @JoinColumn(name = "serverId")
    private Server server;

    private Long userId;

    private int remainingInviteCodeCount;

    private LocalDateTime expiresAt;

    public static Invitation newInvitation(String inviteCode, Server server, Long userId, Long days, int count) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(days);
        return Invitation.builder()
                .inviteCode(inviteCode)
                .server(server)
                .userId(userId)
                .remainingInviteCodeCount(count)
                .expiresAt(expiresAt)
                .build();
    }

    public void decreaseInviteCodeUsage(int usage) {
        this.remainingInviteCodeCount = usage - 1;
    }
}
