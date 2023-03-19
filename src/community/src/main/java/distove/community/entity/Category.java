package distove.community.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    public Category(String name, Server server) {
        this.name = name;
        this.server = server;
    }

    public void updateCategory(String name) {
        this.name = name;
    }

}
