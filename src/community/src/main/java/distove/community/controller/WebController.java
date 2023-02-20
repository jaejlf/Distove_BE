package distove.community.controller;

import distove.community.service.MemberService;
import distove.community.service.ServerService;
import distove.community.web.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WebController {
    private final MemberService memberService;
    private final ServerService serverService;
    @GetMapping("/web/server/ids")
    public List<Long> getServerIdsByUserId(@RequestHeader Long userId) {
        List<Long> serverIds = serverService.getServerIdsByUserId(userId);
        return serverIds;
    }
    @GetMapping("/web/server/{serverId}/users")
    public List<UserResponse> getUsersByServerId(@PathVariable("serverId") Long serverId) {
        List<UserResponse> users = memberService.getUsersByServerId(serverId);
        return users;
    }
}
