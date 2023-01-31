package distove.community.controller;

import distove.community.dto.response.ResultResponse;
import distove.community.dto.response.RoleResponse;
import distove.community.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/roles/{serverId}")
    public ResponseEntity<Object> getRolesByServerId(@RequestHeader("userId") Long userId,
                                                     @PathVariable Long serverId) {
        List<RoleResponse.MemberInfo> result = memberService.getMemberWithRolesByServerId(userId, serverId);
        return ResultResponse.success(
                HttpStatus.OK,
                "서버에 설정된 멤버-역할 리스트 조회",
                result);
    }

    // TODO : CAN_UPDATE_MEMBER_ROLE
    @GetMapping("/server/roles/{serverId}")
    public ResponseEntity<Object> getMemberRoleDetail(@RequestHeader("userId") Long userId,
                                                      @PathVariable Long serverId) {
        List<RoleResponse.Detail> result = memberService.getServerRoleDetail(userId, serverId);
        return ResultResponse.success(
                HttpStatus.OK,
                "서버에 설정된 역할 리스트 조회",
                result);
    }

    // TODO : CAN_UPDATE_MEMBER_ROLE
    @PatchMapping("/member/role/{serverId}")
    public ResponseEntity<Object> updateMemberRole(@RequestHeader("userId") Long userId,
                                                   @PathVariable Long serverId,
                                                   @RequestParam Long roleId,
                                                   @RequestParam Long targetUserId) {
        memberService.updateMemberRole(userId, serverId, roleId, targetUserId);
        return ResultResponse.success(
                HttpStatus.OK, "특정 멤버의 역할 변경", null);
    }

}
