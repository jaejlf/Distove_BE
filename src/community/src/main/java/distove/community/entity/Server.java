package distove.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imgUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "server")
    private List<MemberRole> roles = new ArrayList<>();

    public static Server newServer(String name, String imgUrl) {
        return Server.builder()
                .name(name)
                .imgUrl(imgUrl)
                .build();
    }

    public void updateServer(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
