package distove.community.dto.response;

import distove.community.entity.Category;
import distove.community.entity.CategoryChannel;
import distove.community.entity.Channel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class ServerDto {
    private Long id;
    private String name;
    private List<Category.CategoryIdAndName> categories;
    private List<Channel.ChannelNameAndChannelTypeId> channels;


    public ServerDto(Long id, String name, List<Category.CategoryIdAndName> categories, List<Channel.ChannelNameAndChannelTypeId> channels){
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.channels = channels;
    }
}
