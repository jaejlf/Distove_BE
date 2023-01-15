package distove.community.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Server {
    @Id
//    @Column(name = "server_id")//one to many
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


//    @OneToMany(mappedBy = "server")
//    private List<Member> members = new ArrayList<>();
//
//    @OneToMany(mappedBy = "server")
//    private List<Category> categories = new ArrayList<>();
//
//    @OneToMany(mappedBy = "server")
//    private List<Channel> channels = new ArrayList<>();
//
//    public Server(Long id, String name, List<Category> categories, List<Channel> channels){
//        this.id = id;
//        this.name = name;
//        categories = new ArrayList<>();
//        channels = new ArrayList<>();
//    }
}
