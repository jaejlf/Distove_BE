package distove.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name ="server_id")
    private Server server;
//
//    @OneToMany(mappedBy = "category")
//    private List<Channel> channels = new ArrayList<>();

//    @Builder
//    public Category(String name, Server server){
//        this.name = name;
//        this.server = server;
//    }
    public static Category newCategory(String name, Server server) {
        return Category.builder()
                .name(name)
                .server(server)
                .build();
    }
}
