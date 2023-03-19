package distove.community.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imgUrl;

    public Server(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public void updateServer(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

}
