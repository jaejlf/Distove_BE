package distove.community.dto.response;

import distove.community.entity.Channel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private List<Channel.Info> channels;

    public static CategoryResponse newCategoryResponse(Long id, String name, List<Channel.Info> channels) {
        return CategoryResponse.builder()
                .id(id)
                .name(name)
                .channels(channels)
                .build();
    }

}
