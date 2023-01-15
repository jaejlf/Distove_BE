package distove.community.dto.request;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelRequest {
    private String name;
    private Long serverId;
    private Long channelTypeId;
    private Long categoryId;

}
