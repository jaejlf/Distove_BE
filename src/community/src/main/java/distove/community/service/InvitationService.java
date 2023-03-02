package distove.community.service;

import distove.community.dto.response.InvitationResponse;
import distove.community.entity.Invitation;
import distove.community.entity.Server;
import distove.community.exception.DistoveException;
import distove.community.exception.InvitationException;
import distove.community.repository.InvitationRepository;
import distove.community.repository.ServerRepository;
import distove.community.web.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static distove.community.entity.Invitation.newInvitation;
import static distove.community.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {
    private final UserClient userClient;
    private final ServerRepository serverRepository;
    private final InvitationRepository invitationRepository;
    private final MemberService memberService;

    public String createInvitation(Long userId, Long serverId, Long days, int count) {
        String inviteCode = UUID.randomUUID().toString().substring(0, 8);
        Server server = serverRepository.findById(serverId).orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
        Invitation invitation = newInvitation(inviteCode, server, userId, days, count);
        invitationRepository.save(invitation);
        return inviteCode;
    }

    public void deleteInvitation(Long userId, String inviteCode) {
        Invitation invitation = invitationRepository.findByUserIdAndInviteCode(userId, inviteCode)
                .orElseThrow(() -> new DistoveException(INVITE_CODE_NOT_FOUND));
        invitationRepository.deleteById(invitation.getId());
    }

    public List<InvitationResponse> getInvitations(Long userId, Long serverId) {

        Server server = serverRepository.findById(serverId).orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
        List<Invitation> invitations = invitationRepository.findAllByServer(server);
        List<InvitationResponse> invitationList = new ArrayList<>();
        for (Invitation invitation : invitations) {
            String nickname = userClient.getUser(invitation.getUserId()).getNickname();
            if (userId.equals(invitation.getUserId())) {
                invitationList.add(InvitationResponse.of(invitation, nickname, true));
            } else {
                invitationList.add(InvitationResponse.of(invitation, nickname, false));
            }
        }
        return invitationList;
    }

    public Server joinServerByInviteCode(Long userId, String inviteCode) {

        Invitation invitation = invitationRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new InvitationException(INVITE_CODE_NOT_FOUND));

        validateCode(invitation);
        memberService.joinServer(userId, invitation.getServer().getId());

        return serverRepository.findById(invitation.getServer().getId())
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
    }

    private void validateCode(Invitation invitation) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, invitation.getExpiresAt());

        if (duration.getSeconds() < 0) {
            throw new InvitationException(INVITE_CODE_EXPIRED);
        }

        if (invitation.getRemainingInviteCodeCount() <= 0) {
            throw new InvitationException(INVITE_CODE_USAGE_EXCEEDED);
        }

        invitation.decreaseInviteCodeUsage(invitation.getRemainingInviteCodeCount());
    }

}
