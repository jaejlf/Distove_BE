package distove.community.controller;

import distove.community.dto.response.CategoryInfoResponse;
import distove.community.service.WebService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import distove.community.service.MemberService;
import distove.community.service.ServerService;
import distove.community.web.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WebController {

    private final MemberService memberService;
    private final ServerService serverService;
    private final WebService webService;

    @GetMapping("/web/server/ids")
    public List<Long> getServerIdsByUserId(@RequestHeader Long userId) {
        return serverService.getServerIdsByUserId(userId);
    }

    @GetMapping("/web/server/{serverId}/users")
    public List<UserResponse> getUsersByServerId(@PathVariable("serverId") Long serverId) {
        return memberService.getUsersByServerId(serverId);
    }

    @GetMapping("/web/category/list")
    public List<CategoryInfoResponse> getCategoryIds(@RequestParam List<Long> channelIds) {
        return webService.getCategoryIds(channelIds);
    }

    @GetMapping("/web/category")
    public CategoryInfoResponse getCategoryId(@RequestParam Long channelId) {
        return webService.getCategoryId(channelId);
    }

    @GetMapping("/web/channel/{channelId}/member")
    public boolean checkIsMember(@PathVariable Long channelId, @RequestParam Long userId) {
        return webService.checkIsMember(channelId, userId);
    }

}
