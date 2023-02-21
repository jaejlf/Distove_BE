package distove.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryInfoResponse {

    private Long id;
    private List<Long> channelIds;

    public static CategoryInfoResponse of(Long id, List<Long> channelIds) {
        return CategoryInfoResponse.builder()
                .id(id)
                .channelIds(channelIds)
                .build();
    }

}
