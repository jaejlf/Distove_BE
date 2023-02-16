package distove.community.entity;

import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

    private int uses;

    private LocalDateTime expiresAt;

    private boolean isExpired;


    public static Invitation newInvitation(String inviteCode, Server server, Member member){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(7);
        return Invitation.builder()
                .inviteCode(inviteCode)
                .server(server)
                .member(member)
                .uses(10)
                .expiresAt(expiresAt)
                .isExpired(false)
                .build();
    }

    public void decreaseInviteCodeUsage(int uses) {
        this.uses = uses - 1;
    }
}
