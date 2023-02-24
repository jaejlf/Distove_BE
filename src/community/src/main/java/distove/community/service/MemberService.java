package distove.community.service;

import distove.community.dto.response.MemberResponse;
import distove.community.dto.response.RoleResponse;
import distove.community.entity.Invitation;
import distove.community.entity.Member;
import distove.community.entity.MemberRole;
import distove.community.entity.Server;
import distove.community.exception.DistoveException;
import distove.community.exception.InvitationException;
import distove.community.repository.InvitationRepository;
import distove.community.repository.MemberRepository;
import distove.community.repository.MemberRoleRepository;
import distove.community.repository.ServerRepository;
import distove.community.web.UserClient;
import distove.community.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static distove.community.entity.Member.newMember;
import static distove.community.enumerate.DefaultRoleName.MEMBER;
import static distove.community.enumerate.DefaultRoleName.OWNER;
import static distove.community.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ServerRepository serverRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final InvitationRepository invitationRepository;
    private final UserClient userClient;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;

    public List<Member> getMembersByServerId(Long serverId) {
        checkServerExist(serverId);
        return memberRepository.findMembersByServerId(serverId);
    }
    public List<UserResponse> getUsersByServerId(Long serverId) {
        List<Member> members = memberRepository.findMembersByServerId(serverId);
        List<Long> userIds = members.stream().map(x->x.getUserId()).collect(Collectors.toList());
        List<UserResponse> users = userClient.getUsers(userIds.toString().replace("[","").replace("]",""));
        return users;
    }


    public List<RoleResponse.MemberInfo> getMemberWithRolesByServerId(Long userId, Long serverId) {
        checkServerExist(serverId);
        Member curMember = memberRepository.findByUserIdAndServerId(userId, serverId)
                .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND));

        List<Member> members = getMembersByServerId(serverId);
        List<RoleResponse.MemberInfo> roleResponses = new ArrayList<>();

        for (Member member : members) {
            MemberRole role = member.getRole();

            boolean isActive = getIsActive(serverId, role);
            roleResponses.add(RoleResponse.MemberInfo.builder()
                    .id(member.getUserId())
                    .nickname(userClient.getUser(member.getUserId()).getNickname())
                    .roleName(role.getRoleName())
                    .isActive(isActive)
                    .build());
        }
        return roleResponses;
    }

    public List<RoleResponse.Detail> getServerRoleDetail(Long serverId) {
        List<MemberRole> roles = memberRoleRepository.findAllByServerId(serverId);
        List<RoleResponse.Detail> roleResponses = new ArrayList<>();

        for (MemberRole role : roles) {
            roleResponses.add(RoleResponse.Detail.builder()
                    .roleId(role.getId())
                    .roleName(role.getRoleName())
                    .build());
        }
        return roleResponses;
    }

    public void joinServer(Long userId, Long serverId) {
        if (memberRepository.findByUserIdAndServerId(userId, serverId).isPresent())
            throw new DistoveException(MEMBER_ALREADY_EXIST);

        Server server = checkServerExist(serverId);
        MemberRole memberRole = memberRoleRepository.findByRoleNameAndServerId(MEMBER.getName(), serverId)
                .orElseThrow(() -> new DistoveException(ROLE_NOT_FOUND));
        memberRepository.save(newMember(server, userId, memberRole));
    }

    public void updateMemberRole(Long serverId, Long roleId, Long targetUserId) {
        MemberRole memberRole = memberRoleRepository.findById(roleId)
                .orElseThrow(() -> new DistoveException(ROLE_NOT_FOUND));

        Member target = checkMemberExist(targetUserId, serverId);
        if (!getIsActive(serverId, target.getRole())) throw new DistoveException(CANNOT_CHANGE_ROLE);

        try {
            target.updateRole(memberRole);
            memberRepository.save(target);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new DistoveException(CANNOT_CHANGE_ROLE);
        }
    }

    public MemberResponse getMemberInfo(Long userId, Long serverId) {
        UserResponse userResponse = userClient.getUser(userId);
        Member member = memberRepository.findByUserIdAndServerId(userId, serverId)
                .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND));
        return MemberResponse.of(userResponse, member);
    }

    private Server checkServerExist(Long serverId) {
        return serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
    }

    private Member checkMemberExist(Long userId, Long serverId) {
        return memberRepository.findByUserIdAndServerId(userId, serverId)
                .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND));
    }

    private boolean checkOwnerIsUnique(Long serverId) {
        List<String> roleNames = memberRepository.findMembersByServerId(serverId)
                .stream()
                .map(Member::getRole)
                .map(MemberRole::getRoleName)
                .collect(Collectors.toList());

        return Collections.frequency(roleNames, OWNER.getName()) == 1; // OWNER가 유일할 경우 OWNER 권한 수정 불가
    }

    private boolean getIsActive(Long serverId, MemberRole role) {
        return !Objects.equals(role.getRoleName(), OWNER.getName()) || !checkOwnerIsUnique(serverId);
    }

    @Transactional(noRollbackFor = InvitationException.class)
    public Long validateInviteCode(Long userId, String inviteCode) {

        Invitation invitation = invitationRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new DistoveException(INVITE_CODE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, invitation.getExpiresAt());

        if (duration.getSeconds() < 0) {
            throw new InvitationException(INVITE_CODE_EXPIRED);
        }

        if (invitation.getRemainingInviteCodeCount() <= 0) {
            throw new InvitationException(INVITE_CODE_USAGE_EXCEEDED);
        }

        invitation.decreaseInviteCodeUsage(invitation.getRemainingInviteCodeCount());
        joinServer(userId, invitation.getServer().getId());

        return invitation.getServer().getId();
    }
}
