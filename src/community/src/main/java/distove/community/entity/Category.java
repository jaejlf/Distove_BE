package distove.community.entity;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    public interface CategoryIdAndName {
        Long getId();
        String getName();



    }
}
