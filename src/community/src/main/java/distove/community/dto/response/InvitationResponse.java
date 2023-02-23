package distove.community.dto.response;

import distove.community.entity.Invitation;
import distove.community.web.UserClient;
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
    private int countUsage;
    private LocalDateTime expiresAt;
    private boolean inviter;


    public static InvitationResponse of(Invitation invitation, UserClient userClient, boolean inviter) {
        return InvitationResponse.builder()
                .nickname(userClient.getUser(invitation.getUserId()).getNickname())
                .inviteCode(invitation.getInviteCode())
                .countUsage(invitation.getCountUsage())
                .expiresAt(invitation.getExpiresAt())
                .inviter(inviter)
                .build();
    }
}