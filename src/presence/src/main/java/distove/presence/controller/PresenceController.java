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

    /**
     * 특정 서버에 속한 전체 멤버의 활동상태 조회
     */
    @GetMapping("/server/{serverId}")
    public ResponseEntity<Object> getMemberPresences(@PathVariable Long serverId) {
        List<PresenceResponse> result = presenceService.getMemberPresences(serverId);
        return ResultResponse.success(HttpStatus.OK, "멤버 활동상태 조회", result);
    }

}
