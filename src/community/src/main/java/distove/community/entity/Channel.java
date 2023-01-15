package distove.community.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
@Getter
@Entity
@RequiredArgsConstructor
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long channelTypeId;

    @ManyToOne
    @JoinColumn(name ="server_id")
    private Server server;


//    @OneToOne(mappedBy = "channel")
//    @JoinColumn(name ="channel_id")
//    private CategoryChannel categoryChannel;

    public Channel(String name, Long channelTypeId, Server server){
        this.name = name;
        this.channelTypeId = channelTypeId;
        this.server = server;
    }
    public interface ChannelNameAndChannelTypeId{
        Long getId();
        String getName();

        Long getChannelTypeId();
//        CategoryChannel getCategoryChannel();
    }
}
