package distove.presence.controller;

import distove.presence.config.RequestUser;
import distove.presence.dto.response.PresenceResponse;
import distove.presence.dto.response.ResultResponse;
import distove.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/server/{serverId}")
    public ResponseEntity<Object> getMemberPresencesByServerId(@RequestUser Long userId,
                                                               @PathVariable("serverId") Long serverId) {
        List<PresenceResponse> presenceResponses = presenceService.getMemberPresencesByServerId(serverId);
        return ResultResponse.success(HttpStatus.OK, "멤버 활동상태 조회 성공", presenceResponses);
    }

}
