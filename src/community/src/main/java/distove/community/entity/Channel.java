package distove.community.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer channelTypeId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Channel(String name, Integer channelTypeId, Category category) {
        this.name = name;
        this.channelTypeId = channelTypeId;
        this.category = category;
    }

    public void updateChannel(String name) {
        this.name = name;
    }

}
