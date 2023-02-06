package distove.community.entity;

import lombok.*;

import javax.persistence.*;


@Builder
@Getter
@Entity
@AllArgsConstructor
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

    public static Channel newChannel(String name, Integer channelTypeId, Category category) {
        return Channel.builder()
                .name(name)
                .channelTypeId(channelTypeId)
                .category(category)
                .build();
    }

    public void updateChannel(String name) {
        this.name = name;
    }

    public interface Info {
        Long getId();

        String getName();

        Integer getChannelTypeId();
    }
}
