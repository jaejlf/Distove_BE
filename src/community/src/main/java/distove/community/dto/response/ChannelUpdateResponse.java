package distove.community.dto.response;

import lombok.Getter;

@Getter
public class ChannelUpdateResponse {
    Long id;
    String name;
    Integer channelTypeId;

    public ChannelUpdateResponse(Long id, String name, Integer channelTypeId){
        this.id = id;
        this.name = name;
        this.channelTypeId = channelTypeId;
    }

}
