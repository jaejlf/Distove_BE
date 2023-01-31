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

    /**
     * TODO
     * 현재 유저들 역할 GET
     * 역할 변경
     * 현재 서버의 역할 내려주기 (활성/비활성) -- 유저 id별
     */

    private final MemberService memberService;

    @GetMapping("/member/roles/{serverId}")
    public ResponseEntity<Object> getRolesByServerId(@RequestHeader("userId") Long userId,
                                                     @PathVariable Long serverId) {
        List<RoleResponse.MemberInfo> result = memberService.getMemberWithRolesByServerId(userId, serverId);
        return ResultResponse.success(
                HttpStatus.OK,
                "서버에 설정된 멤버-역할 리스트",
                result);
    }

    @GetMapping("/server/roles/{serverId}")
    public ResponseEntity<Object> getMemberRoleDetail(@RequestHeader("userId") Long userId,
                                                      @PathVariable Long serverId) {
        List<RoleResponse.Detail> result = memberService.getServerRoleDetail(userId, serverId);
        return ResultResponse.success(
                HttpStatus.OK,
                "서버에 설정된 역할 리스트",
                result);
    }

    @PatchMapping("/member/role/{serverId}")
    public ResponseEntity<Object> updateMemberRole(@RequestHeader("userId") Long userId,
                                                   @PathVariable Long serverId,
                                                   @RequestParam Long roleId) {
        memberService.updateMemberRole(userId, serverId, roleId);
        return ResultResponse.success(
                HttpStatus.OK, "특정 멤버의 역할 변경", null);
    }

}
