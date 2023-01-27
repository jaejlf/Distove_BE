package distove.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedMessageResponse {

    private int totalPage;
    private ReplyInfoResponse replyInfo;
    private List<MessageResponse> messages;

    public static PagedMessageResponse ofDefault(int totalPage, List<MessageResponse> messageResponses) {
        return PagedMessageResponse.builder()
                .totalPage(totalPage)
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

}
