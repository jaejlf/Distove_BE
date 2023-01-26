package distove.chat.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReplyInfo {

    private String replyName;
    private Long stUserId;
    private String nickname;
    private String profileImgUrl;

    public static ReplyInfo newReplyInfo(String replyName, Long stUserId) {
        return ReplyInfo.builder()
                .replyName(replyName)
                .stUserId(stUserId)
                .build();
    }

    public static ReplyInfo withUserDetails(String replyName, Long stUserId, String nickname, String profileImgUrl) {
        return ReplyInfo.builder()
                .replyName(replyName)
                .stUserId(stUserId)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .build();
    }

}
