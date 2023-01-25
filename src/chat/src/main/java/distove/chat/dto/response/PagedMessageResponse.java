package distove.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PagedMessageResponse {

    private int totalPage;
    private List<MessageResponse> messages;

    public static PagedMessageResponse of(int totalPage, List<MessageResponse> messageResponses) {
        return PagedMessageResponse.builder()
                .totalPage(totalPage)
                .messages(messageResponses)
                .build();
    }

}
