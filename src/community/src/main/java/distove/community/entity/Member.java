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

    private String name;
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name ="server_id")
    public Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private User user;

//    public interface UserServer{
//        Long getServer().getId();
//    }

}
