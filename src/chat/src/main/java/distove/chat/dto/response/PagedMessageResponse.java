package distove.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedMessageResponse {

    private UnreadInfo unread;
    private ReplyInfoResponse replyInfo;
    private String previousCursorId;
    private String nextCursorId;
    private List<MessageResponse> messages;

    public static PagedMessageResponse ofDefault(UnreadInfo unread, List<MessageResponse> messages) {
        return PagedMessageResponse.builder()
                .unread(unread)
//                .previousCursorId(previousCursorId)
//                .nextCursorId(nextCursorId)
                .messages(messages)
                .build();
    }

    public static PagedMessageResponse ofChild(ReplyInfoResponse replyInfo, List<MessageResponse> messages) {
        return PagedMessageResponse.builder()
                .replyInfo(replyInfo)
               // .hasPrevious(hasPrevious)
               // .hasNext(hasNext)
                .messages(messages)
                .build();
    }

    @Getter
    @Builder
    public static class UnreadInfo {

        private LocalDateTime latestConnectedAt;
        private int count;
        private String messageId;

        public static UnreadInfo of(LocalDateTime latestConnectedAt, int count, String messageId) {
            return UnreadInfo.builder()
                    .latestConnectedAt(latestConnectedAt)
                    .count(count)
                    .messageId(messageId)
                    .build();
        }

    }

}
