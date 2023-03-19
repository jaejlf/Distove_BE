package distove.community.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    private String code;
    private Long userId;
    private int count = 10;
    private LocalDateTime expiredAt;

    public Invitation(String code, Server server, Long userId) {
        this.code = code;
        this.server = server;
        this.userId = userId;
        this.expiredAt = LocalDateTime.now().plusDays(10);
    }

    public void updateCount(int count) {
        this.count = count;
    }

}
