package distove.community.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@RequiredArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name ="server_id")
    private Server server;
//
//    @OneToMany(mappedBy = "category")
//    private List<Channel> channels = new ArrayList<>();


    public Category(String name, Server server){
        this.name = name;
        this.server = server;
    }

//    public interface CategoryWithOutServer{
//        Long getId();
//        String getName();
//
//    }
}
