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

    private UnreadInfoResponse unread;
    private ThreadInfoResponse threadInfo;
    private CursorInfoResponse cursorInfo;
    private List<MessageResponse> messages;

    public static PagedMessageResponse ofDefault(List<MessageResponse> messages, UnreadInfoResponse unread, CursorInfoResponse cursorInfo) {
        return PagedMessageResponse.builder()
                .unread(unread)
                .cursorInfo(cursorInfo)
                .messages(messages)
                .build();
    }

    public static PagedMessageResponse ofChild(List<MessageResponse> messages, ThreadInfoResponse threadInfo) {
        return PagedMessageResponse.builder()
                .threadInfo(threadInfo)
                .messages(messages)
                .build();
    }

    @Getter
    @Builder
    public static class UnreadInfoResponse {

        private LocalDateTime lastReadAt;
        private int unreadCount;
        private String messageId; // 최상단 안 읽은 메시지 id

        public static UnreadInfoResponse of(LocalDateTime lastReadAt, int count, String messageId) {
            return UnreadInfoResponse.builder()
                    .lastReadAt(lastReadAt)
                    .unreadCount(count)
                    .messageId(messageId)
                    .build();
        }

    }

    @Getter
    @Builder
    public static class CursorInfoResponse {

        private String previousCursorId;
        private String nextCursorId;

        public static CursorInfoResponse of(String previousCursorId, String nextCursorId) {
            return CursorInfoResponse.builder()
                    .previousCursorId(previousCursorId)
                    .nextCursorId(nextCursorId)
                    .build();
        }

    }

}
