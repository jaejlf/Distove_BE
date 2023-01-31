package distove.community.service;

import distove.community.dto.response.RoleResponse;
import distove.community.entity.Member;
import distove.community.entity.MemberRole;
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

    public List<RoleResponse.Info> getMemberWithRolesByServerId(Long userId, Long serverId) {
        checkServerExist(serverId);
        List<Member> members = getMembersByServerId(serverId);
        List<RoleResponse.Info> roleResponses = new ArrayList<>();
        for (Member member : members) {
            MemberRole role = member.getRole();
            roleResponses.add(RoleResponse.Info.builder()
                    .userId(member.getUserId())
                    .nickname(userClient.getUser(member.getUserId()).getNickname())
                    .roleName(role.getRoleName()).build());
        }
        return roleResponses;
    }

    public List<RoleResponse.Detail> getServerRoleDetail(Long userId, Long serverId) {
        checkServerExist(serverId);
        checkAuthorization(userId, serverId);
        List<MemberRole> roles = memberRoleRepository.findAllByServerId(serverId);
        List<RoleResponse.Detail> roleResponses = new ArrayList<>();
        for (MemberRole role : roles) {
            roleResponses.add(RoleResponse.Detail.builder()
                    .roleId(role.getId())
                    .roleName(role.getRoleName())
                    .isActive(!Objects.equals(role.getRoleName(), OWNER.getName()))
                    .build());
        }
        return roleResponses;
    }

    private void checkServerExist(Long serverId) {
        serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
    }

    private void checkAuthorization(Long userId, Long serverId) {
        Member member = memberRepository.findByUserIdAndServerId(userId, serverId)
                .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND_ERROR));

        if (Objects.equals(member.getRole().getRoleName(), MEMBER.getName())) throw new DistoveException(NO_AUTH_ERROR);
    }

}
