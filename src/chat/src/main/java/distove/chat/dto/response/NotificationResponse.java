package distove.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import distove.chat.client.dto.CategoryInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {

    private Long serverId;
    private CategoryInfoResponse category;
    private List<CategoryInfoResponse> categories;

    public static NotificationResponse ofUnreads(Long serverId, List<CategoryInfoResponse> categories) {
        return NotificationResponse.builder()
                .serverId(serverId)
                .categories(categories)
                .build();
    }

    public static NotificationResponse ofNewMessage(Long serverId, CategoryInfoResponse category) {
        return NotificationResponse.builder()
                .serverId(serverId)
                .category(category)
                .build();
    }

}
