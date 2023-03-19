package distove.community.dto.response;

import distove.community.entity.Invitation;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Builder
@Getter
public class InvitationResponse {

    private String nickname;
    private String code;
    private int count;
    private LocalDateTime expiredAt;
    private boolean hasAuthorized;

    public static InvitationResponse of(Invitation invitation, String nickname, boolean hasAuthorized) {
        return InvitationResponse.builder()
                .nickname(nickname)
                .code(invitation.getCode())
                .count(invitation.getCount())
                .expiredAt(invitation.getExpiredAt())
                .hasAuthorized(hasAuthorized)
                .build();
    }

}
