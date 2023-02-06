package distove.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private List<ChannelResponse> channels;

    public static CategoryResponse newCategoryResponse(Long id, String name, List<ChannelResponse> channels) {
        return CategoryResponse.builder()
                .id(id)
                .name(name)
                .channels(channels)
                .build();
    }

}
