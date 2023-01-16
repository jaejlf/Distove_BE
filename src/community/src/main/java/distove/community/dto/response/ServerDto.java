package distove.community.dto.response;

import distove.community.entity.Category;
import distove.community.entity.Channel;
import lombok.Getter;

import java.util.List;
@Getter
public class ServerDto {
    private Long id;
    private String name;
    private List<Category> categories;
    private List<Channel> channels;


    public ServerDto(Long id, String name, List<Category> categories, List<Channel> channels){
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.channels = channels;
    }
}
