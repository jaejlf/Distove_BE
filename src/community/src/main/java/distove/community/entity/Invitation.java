package distove.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    private int countUsage;

    private LocalDateTime expiresAt;

    private boolean isExpired;

    public static Invitation newInvitation(String inviteCode, Server server, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(1);
        return Invitation.builder()
                .inviteCode(inviteCode)
                .server(server)
                .userId(userId)
                .countUsage(3)
                .expiresAt(expiresAt)
                .isExpired(false)
                .build();
    }

    public void decreaseInviteCodeUsage(int usage) {
        this.countUsage = usage - 1;
    }

    public void isExpired() {
        this.isExpired = true;
    }
}
