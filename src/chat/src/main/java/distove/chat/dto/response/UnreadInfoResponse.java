package distove.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UnreadInfoResponse {

    private LocalDateTime latestConnectedAt;
    private int count;
    private String messageId;
    private int page;

    public static UnreadInfoResponse of(LocalDateTime latestConnectedAt, int count, String messageId, int page) {
        return UnreadInfoResponse.builder()
                .latestConnectedAt(latestConnectedAt)
                .count(count)
                .messageId(messageId)
                .page(page)
                .build();
    }

}
