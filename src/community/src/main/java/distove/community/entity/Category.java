package distove.community.entity;

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name ="server_id")
    private Server server;
}
