package distove.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReplyInfoResponse {

    private String replyName;
    private Long stUserId;
    private String nickname;
    private String profileImgUrl;

    public static ReplyInfoResponse of(String replyName, Long stUserId, String nickname, String profileImgUrl) {
        return ReplyInfoResponse.builder()
                .replyName(replyName)
                .stUserId(stUserId)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .build();
    }

}
