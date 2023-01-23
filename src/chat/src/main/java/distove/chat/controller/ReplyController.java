package distove.chat.controller;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.request.ReplyRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.enumerate.MessageType;
import distove.chat.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReplyController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ReplyService replyService;

    // TODO : Response 형식 논의 필요 (void or else?)
    @PostMapping("/reply/{parentId}")
    public void createReply(@RequestHeader("userId") Long userId,
                            @PathVariable String parentId,
                            @Valid @RequestBody ReplyRequest request) {
        replyService.createReply(userId, parentId, request);
    }

    // TODO : Pub & Sub path 논의 필요
    @MessageMapping("/reply/{channelId}")
    public void publishMessage(@DestinationVariable Long channelId, @Payload MessageRequest request) {
        MessageResponse result = replyService.publishMessage(channelId, request);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, result);
    }

    @PostMapping("/reply/file/{channelId}")
    public void publishFile(@PathVariable Long channelId,
                            @RequestParam MessageType type,
                            @ModelAttribute FileUploadRequest request) {
        MessageResponse result = replyService.publishFile(channelId, type, request);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, result);
    }

    @GetMapping("/replies/{channelId}")
    public ResponseEntity<Object> getRepliesByChannelId(@RequestHeader("userId") Long userId,
                                                        @PathVariable Long channelId) {
        List<MessageResponse> result = replyService.getRepliesByChannelId(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "채널의 Reply 리스트 조회", result);
    }

    @GetMapping("/children")
    public ResponseEntity<Object> getChildrenByParentId(@RequestHeader("userId") Long userId,
                                                        @RequestParam String parentId) {
        List<MessageResponse> result = replyService.getChildrenByParentId(userId, parentId);
        return ResultResponse.success(HttpStatus.OK, "부모 메시지의 Reply 리스트 조회", result);
    }

}
