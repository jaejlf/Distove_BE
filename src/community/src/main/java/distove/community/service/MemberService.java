package distove.community.service;

import distove.community.dto.response.MemberResponse;
import distove.community.dto.response.RoleResponse;
import distove.community.entity.Member;
import distove.community.entity.MemberRole;
import distove.community.entity.Server;
import distove.community.exception.DistoveException;
import distove.community.repository.MemberRepository;
import distove.community.repository.MemberRoleRepository;
import distove.community.repository.ServerRepository;
import distove.community.web.UserClient;
import distove.community.web.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static distove.community.entity.Member.newMember;
import static distove.community.enumerate.DefaultRoleName.MEMBER;
import static distove.community.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ServerRepository serverRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final UserClient userClient;

    public List<Member> getMembersByServerId(Long serverId) {
        return memberRepository.findMembersByServerId(serverId);
    }

    public List<RoleResponse.MemberInfo> getMemberWithRolesByServerId(Long userId, Long serverId) {
        checkServerExist(serverId);
        Member curMember = memberRepository.findByUserIdAndServerId(userId, serverId)
                .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND));

        List<Member> members = getMembersByServerId(serverId);
        List<RoleResponse.MemberInfo> roleResponses = new ArrayList<>();
        for (Member member : members) {
            MemberRole role = member.getRole();
            roleResponses.add(RoleResponse.MemberInfo.builder()
                    .id(member.getUserId())
                    .nickname(userClient.getUser(member.getUserId()).getNickname())
                    .roleName(role.getRoleName())
                    .isActive(curMember.getRole().isCanUpdateMemberRole())
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
        target.updateRole(memberRole);
        memberRepository.save(target);
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

}
