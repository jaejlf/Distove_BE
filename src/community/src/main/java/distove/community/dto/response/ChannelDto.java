package distove.community.dto.response;

import lombok.Getter;
@Getter
public class ChannelDto {
    private Long id;
    private String name;
    private Integer channelTypeId;


    public ChannelDto(Long id, String name, Integer channelTypeId){
        this.id = id;
        this.name = name;
        this.channelTypeId = channelTypeId;
    }
}
