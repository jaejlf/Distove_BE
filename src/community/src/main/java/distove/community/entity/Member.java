package distove.community.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "server_id")
    public Server server;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private MemberRole role;

    public static Member newMember(Server server, Long userId, MemberRole role) {
        return Member.builder()
                .server(server)
                .userId(userId)
                .role(role)
                .build();
    }

    public void updateRole(MemberRole role) {
        this.role = role;
    }

}
