package distove.presence.controller;

import distove.presence.dto.response.PresenceResponse;
import distove.presence.dto.response.ResultResponse;
import distove.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/server/{serverId}")
    public ResponseEntity<Object> getMemberPresencesByServerId(@RequestHeader("userId") Long userId,
                                                               @PathVariable("serverId") Long serverId) {
        List<PresenceResponse> presenceResponses = presenceService.getMemberPresencesByServerId(serverId);
        return ResultResponse.success(HttpStatus.OK, "멤버 활동상태 조회 성공", presenceResponses);
    }

//    @PutMapping("/subscribe/server/{serverId}")
//    public ResponseEntity<Object> subscribeServerPresence(@RequestHeader("userId") Long userId,
//                                                          @PathVariable("serverId") Long serverId) {
//        presenceService.subscribeServerPresence(userId,serverId);
//        return ResultResponse.success(HttpStatus.OK, "구독", presenceResponses);
//    }
//    @PutMapping("/unsubscribe/server/{serverId}")
//    public ResponseEntity<Object> unsubscribeServerPresence(@RequestHeader("userId") Long userId,
//                                                          @PathVariable("serverId") Long serverId) {
//        presenceService.unsubscribeServerPresence(userId,serverId);
//        return ResultResponse.success(HttpStatus.OK, "un구독", presenceResponses);
//    }
}
