package distove.chat.controller;

import distove.chat.dto.request.ReplyRequest;
import distove.chat.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class ReplyController {

    private final ReplyService replyService;

    // TODO : Response 형식 논의 필요 (void or else?)
    @PostMapping("/reply/{parentId}")
    public void createReply(@RequestHeader("userId") Long userId,
                            @PathVariable String parentId,
                            @Valid @RequestBody ReplyRequest request) {
        replyService.createReply(userId, parentId, request);
    }

}
