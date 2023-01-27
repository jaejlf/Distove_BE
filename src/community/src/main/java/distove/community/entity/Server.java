package distove.community.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@RequiredArgsConstructor
public class Server {
    @Id
//    @Column(name = "server_id")//one to many
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imgUrl;


//    @OneToMany(mappedBy = "server")
//    private List<Member> members = new ArrayList<>();

//    @OneToMany(mappedBy = "server")
////    @JoinColumn(name ="category_id")
//    private List<Category> categories = new ArrayList<>();

//    @OneToMany(mappedBy = "server")
//    private List<Channel> channels = new ArrayList<>();

    public Server(String name,String imgUrl){
        this.name = name;
        this.imgUrl = imgUrl;
    }
    public void updateServer(String name,String imgUrl){
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
