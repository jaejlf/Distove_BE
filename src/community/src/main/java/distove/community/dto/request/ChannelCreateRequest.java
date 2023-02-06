package distove.community.dto.request;


import lombok.Getter;

@Getter
public class ChannelCreateRequest {
    private String name;
    private Long categoryId;
    private Integer channelTypeId;

}
