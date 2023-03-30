package distove.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedMessageResponse {

    private UnreadInfoResponse unread;
    private ThreadInfoResponse threadInfo;
    private String previousCursorId;
    private String nextCursorId;
    private List<MessageResponse> messages;

    public static PagedMessageResponse ofDefault(UnreadInfoResponse unread, List<MessageResponse> messages, Map<String, String> cursorIdInfo) {
        return PagedMessageResponse.builder()
                .unread(unread)
                .previousCursorId(cursorIdInfo.get("previousCursorId"))
                .nextCursorId(cursorIdInfo.get("nextCursorId"))
                .messages(messages)
                .build();
    }

    public static PagedMessageResponse ofChild(ThreadInfoResponse threadInfo, List<MessageResponse> messages) {
        return PagedMessageResponse.builder()
                .threadInfo(threadInfo)
                .messages(messages)
                .build();
    }

}
