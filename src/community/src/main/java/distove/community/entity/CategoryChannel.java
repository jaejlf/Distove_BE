package distove.community.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
@Getter
@Entity
@RequiredArgsConstructor
public class CategoryChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;

    @OneToOne//(mappedBy = "channel")
    @JoinColumn(name ="channel_id")
    private Channel channel;


    public CategoryChannel(Category category, Channel channel){
        this.category = category;
        this.channel = channel;
    }
}
