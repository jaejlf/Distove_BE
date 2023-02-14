package distove.community.controller;

import distove.community.config.AuthorizedRole;
import distove.community.config.RequestUser;
import distove.community.dto.response.MemberResponse;
import distove.community.dto.response.ResultResponse;
import distove.community.dto.response.RoleResponse;
import distove.community.entity.Member;
import distove.community.service.MemberService;
import distove.community.web.UserClient;
import distove.community.web.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static distove.community.config.AuthorizedRole.Auth.CAN_UPDATE_MEMBER_ROLE;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final UserClient userClient;

    @GetMapping("/member")
    public ResponseEntity<Object> getMemberInfo(@RequestUser Long userId,
                                                @RequestParam Long serverId) {
        MemberResponse result = memberService.getMemberInfo(userId, serverId);
        return ResultResponse.success(HttpStatus.OK, "현재 멤버 정보 조회", result);
    }

    @GetMapping("/server/{serverId}/users")
    public List<UserResponse> getUsersByServerId(@PathVariable("serverId") Long serverId) {
        List<UserResponse> users = memberService.getUsersByServerId(serverId);
        return users;
    }

    @GetMapping("/member/roles/{serverId}")
    public ResponseEntity<Object> getRolesByServerId(@RequestUser Long userId,
                                                     @PathVariable Long serverId) {
        List<RoleResponse.MemberInfo> result = memberService.getMemberWithRolesByServerId(userId, serverId);
        return ResultResponse.success(HttpStatus.OK, "멤버별 역할 리스트 조회", result);
    }

    @AuthorizedRole(name = CAN_UPDATE_MEMBER_ROLE)
    @GetMapping("/server/roles/{serverId}")
    public ResponseEntity<Object> getMemberRoleDetail(@PathVariable Long serverId) {
        List<RoleResponse.Detail> result = memberService.getServerRoleDetail(serverId);
        return ResultResponse.success(HttpStatus.OK, "설정 가능한 역할 리스트 조회", result);
    }

    @AuthorizedRole(name = CAN_UPDATE_MEMBER_ROLE)
    @PatchMapping("/member/role/{serverId}")
    public ResponseEntity<Object> updateMemberRole(@PathVariable Long serverId,
                                                   @RequestParam Long roleId,
                                                   @RequestParam Long targetUserId) {
        memberService.updateMemberRole(serverId, roleId, targetUserId);
        return ResultResponse.success(HttpStatus.OK, "멤버 역할 변경", null);
    }

    // TODO : 초대 코드 로직 반영 X
    @PostMapping("/server/join/{serverId}")
    public ResponseEntity<Object> joinServer(@RequestUser Long userId,
                                             @PathVariable("serverId") Long serverId) {
        memberService.joinServer(userId, serverId);
        return ResultResponse.success(HttpStatus.OK, "서버 초대 수락", null);
    }

}
