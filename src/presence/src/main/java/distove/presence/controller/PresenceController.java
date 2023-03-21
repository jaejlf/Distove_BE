package distove.presence.controller;

import distove.presence.dto.response.PresenceResponse;
import distove.presence.dto.response.ResultResponse;
import distove.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/server/{serverId}")
    public ResponseEntity<Object> getMemberPresences(@PathVariable Long serverId) {
        List<PresenceResponse> presenceResponses = presenceService.getMemberPresencesByServerId(serverId);
        return ResultResponse.success(
                HttpStatus.OK,
                serverId + "번 서버의 멤버별 활동상태 조회",
                presenceResponses);
    }

}
