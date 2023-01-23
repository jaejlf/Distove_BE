package distove.chat.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReplyInfo {

    private String replyName;
    private Long stUserId;

    public static ReplyInfo newReplyInfo(String replyName, Long stUserId) {
        return ReplyInfo.builder()
                .replyName(replyName)
                .stUserId(stUserId)
                .build();
    }

}
