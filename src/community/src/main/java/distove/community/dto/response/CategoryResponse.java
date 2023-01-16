package distove.community.dto.response;

import distove.community.entity.Channel;
import lombok.Getter;

import java.util.List;

@Getter
public class CategoryResponse {
    private Long id;
    private String name;
    private List<Channel.Info> channels;


    public CategoryResponse(Long id, String name, List<Channel.Info> channels){
        this.id = id;
        this.name = name;
        this.channels = channels;

    }
}
