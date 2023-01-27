package distove.community.entity;

import lombok.*;

import javax.persistence.*;
@Getter
//@NamedEntityGraph(name="Member.server",attributeNodes = @NamedAttributeNode("server"))
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="server_id")
    public Server server;

    private Long userId;

//    public interface UserServer{
//        Long getServer().getId();
//    }

}
