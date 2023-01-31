package distove.community.controller;

import distove.community.config.AuthorizedRole;
import distove.community.dto.response.ResultResponse;
import distove.community.dto.response.RoleResponse;
import distove.community.entity.Member;
import distove.community.service.MemberService;
import distove.community.web.UserClient;
import distove.community.web.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static distove.community.config.AuthorizedRole.Auth.CAN_UPDATE_MEMBER_ROLE;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final UserClient userClient;

    @GetMapping("/server/{serverId}/member/list")
    public ResponseEntity<Object> getMembersByServerId(@PathVariable("serverId") Long serverId) {
        List<UserResponse> users = new ArrayList<>();
        List<Member> members = memberService.getMembersByServerId(serverId);
        for (Member member : members) {
            users.add(userClient.getUser(member.getUserId()));
        }
        return ResultResponse.success(HttpStatus.OK, "서버 내 멤버 리스트 조회", users);
    }

    @GetMapping("/member/roles/{serverId}")
    public ResponseEntity<Object> getRolesByServerId(@RequestHeader("userId") Long userId,
                                                     @PathVariable Long serverId) {
        List<RoleResponse.MemberInfo> result = memberService.getMemberWithRolesByServerId(userId, serverId);
        return ResultResponse.success(HttpStatus.OK, "서버에 설정된 멤버-역할 리스트 조회", result);
    }

    @AuthorizedRole(name = CAN_UPDATE_MEMBER_ROLE)
    @GetMapping("/server/roles/{serverId}")
    public ResponseEntity<Object> getMemberRoleDetail(@RequestHeader("userId") Long userId,
                                                      @PathVariable Long serverId) {
        List<RoleResponse.Detail> result = memberService.getServerRoleDetail(userId, serverId);
        return ResultResponse.success(HttpStatus.OK, "서버에 설정된 역할 리스트 조회", result);
    }

    @AuthorizedRole(name = CAN_UPDATE_MEMBER_ROLE)
    @PatchMapping("/member/role/{serverId}")
    public ResponseEntity<Object> updateMemberRole(@RequestHeader("userId") Long userId,
                                                   @PathVariable Long serverId,
                                                   @RequestParam Long roleId,
                                                   @RequestParam Long targetUserId) {
        memberService.updateMemberRole(userId, serverId, roleId, targetUserId);
        return ResultResponse.success(HttpStatus.OK, "특정 멤버의 역할 변경", null);
    }

    // TODO : 초대 코드 로직 반영 X
    @PostMapping("/server/join/{serverId}")
    public ResponseEntity<Object> joinServer(@RequestHeader("userId") Long userId,
                                             @PathVariable("serverId") Long serverId) {
        memberService.joinServer(userId, serverId);
        return ResultResponse.success(HttpStatus.OK, "서버 초대 수락", null);
    }

}
