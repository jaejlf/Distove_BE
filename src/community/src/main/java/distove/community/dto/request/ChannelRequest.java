package distove.community.dto.request;


import lombok.Getter;

@Getter
public class ChannelRequest {
    private String name;
    private Long categoryId;
    private Integer channelTypeId;
    protected ChannelRequest(){}
}
