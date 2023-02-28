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
    private String inviteCode;
    private int remainingInviteCodeCount;
    private LocalDateTime expiresAt;
    private boolean inviter;


    public static InvitationResponse of(Invitation invitation, String nickname, boolean inviter) {
        return InvitationResponse.builder()
                .nickname(nickname)
                .inviteCode(invitation.getInviteCode())
                .remainingInviteCodeCount(invitation.getRemainingInviteCodeCount())
                .expiresAt(invitation.getExpiresAt())
                .inviter(inviter)
                .build();
    }
}
