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

    @ManyToOne
    @JoinColumn(name = "server_id")
    public Server server;

    private Long userId;

    public static Member newMember(Server server, Long userId) {
        return Member.builder()
                .server(server)
                .userId(userId)
                .build();
    }

}
