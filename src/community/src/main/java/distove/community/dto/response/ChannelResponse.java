package distove.community.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelResponse {
    private Long id;
    private String name;
    private Integer channelTypeId;

    public static ChannelResponse newChannelResponse(Long id, String name, Integer channelTypeId) {
        return ChannelResponse.builder()
                .id(id)
                .name(name)
                .channelTypeId(channelTypeId)
                .build();
    }
}
