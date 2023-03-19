package distove.community.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "server_id")
    public Server server;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private MemberRole role;

    @Version
    private Long version;

    public Member(Server server, Long userId, MemberRole role) {
        this.server = server;
        this.userId = userId;
        this.role = role;
    }

    public void updateRole(MemberRole role) {
        this.role = role;
    }

}
