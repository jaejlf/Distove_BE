package distove.community.dto.response;

import distove.community.entity.Category;
import distove.community.entity.Channel;
import lombok.Getter;

import java.util.List;
@Getter
public class ChannelDto {
    private Long id;
    private String name;

    private Long channelTypeId;
    private Long categoryId;


    public ChannelDto(Long id, String name, Long channelTypeId, Long categoryId){
        this.id = id;
        this.name = name;
        this.channelTypeId = channelTypeId;
        this.categoryId = categoryId;
    }
}
