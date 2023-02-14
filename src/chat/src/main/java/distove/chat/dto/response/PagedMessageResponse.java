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

    private int totalPage;
    private UnreadInfo unread;
    private ReplyInfoResponse replyInfo;
    private List<MessageResponse> messages;

    public static PagedMessageResponse ofDefault(int totalPage, UnreadInfo unread, List<MessageResponse> messageResponses) {
        return PagedMessageResponse.builder()
                .totalPage(totalPage)
                .unread(unread)
                .messages(messageResponses)
                .build();
    }

    public static PagedMessageResponse ofChild(int totalPage, ReplyInfoResponse replyInfo, List<MessageResponse> messageResponses) {
        return PagedMessageResponse.builder()
                .totalPage(totalPage)
                .replyInfo(replyInfo)
                .messages(messageResponses)
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
