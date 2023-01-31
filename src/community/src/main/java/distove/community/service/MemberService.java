package distove.community.service;

import distove.community.dto.response.RoleResponse;
import distove.community.entity.Member;
import distove.community.entity.MemberRole;
import distove.community.entity.Server;
import distove.community.exception.DistoveException;
import distove.community.repository.MemberRepository;
import distove.community.repository.MemberRoleRepository;
import distove.community.repository.ServerRepository;
import distove.community.web.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static distove.community.entity.Member.newMember;
import static distove.community.enumerate.DefaultRoleName.MEMBER;
import static distove.community.enumerate.DefaultRoleName.OWNER;
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
        List<Member> members = getMembersByServerId(serverId);
        List<RoleResponse.MemberInfo> roleResponses = new ArrayList<>();
        for (Member member : members) {
            MemberRole role = member.getRole();
            roleResponses.add(RoleResponse.MemberInfo.builder()
                    .id(member.getUserId())
                    .nickname(userClient.getUser(member.getUserId()).getNickname())
                    .roleName(role.getRoleName()).build());
        }
        return roleResponses;
    }

    public List<RoleResponse.Detail> getServerRoleDetail(Long userId, Long serverId) {
        checkServerExist(serverId);
        Member member = memberRepository.findByUserIdAndServerId(userId, serverId)
                .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND_ERROR));

        List<MemberRole> roles = memberRoleRepository.findAllByServerId(serverId);
        List<RoleResponse.Detail> roleResponses = new ArrayList<>();

        boolean isActive = false;
        for (MemberRole role : roles) {
            isActive = updateActiveIfHasAuthorization(member, isActive, role);
            roleResponses.add(RoleResponse.Detail.builder()
                    .roleId(role.getId())
                    .roleName(role.getRoleName())
                    .isActive(isActive)
                    .build());
        }
        return roleResponses;
    }

    public void joinServer(Long userId, Long serverId) {
        Server server = checkServerExist(serverId);
        checkMemberExist(userId, server);
        MemberRole memberRole = memberRoleRepository.findByRoleNameAndServerId(MEMBER.getName(), server.getId())
                .orElseThrow(() -> new DistoveException(ROLE_NOT_FOUND_ERROR));
        memberRepository.save(newMember(server, userId, memberRole));
    }

    private Server checkServerExist(Long serverId) {
        return serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
    }

    private void checkMemberExist(Long userId, Server server) {
        if (memberRepository.findByUserIdAndServerId(userId, server.getId()).isPresent())
            throw new DistoveException(MEMBER_ALREADY_EXIST_ERROR);
    }

    private static boolean updateActiveIfHasAuthorization(Member member, boolean isActive, MemberRole role) {
        if (!Objects.equals(member.getRole().getRoleName(), MEMBER.getName())) {
            isActive = !Objects.equals(role.getRoleName(), OWNER.getName());
        }
        return isActive;
    }

}
