package distove.chat.controller;

import distove.chat.config.RequestUser;
import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.enumerate.MessageType;
import distove.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReplyController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @Value("${sub.destination}")
    private String destination;

    @MessageMapping("/reply/{channelId}")
    public void publishMessage(@Header("userId") Long userId,
                               @DestinationVariable Long channelId,
                               @Payload MessageRequest request) {
        if (request.getReplyName() != null) {
            simpMessagingTemplate.convertAndSend(
                    destination + channelId,
                    messageService.createReply(userId, request));
        }

        MessageResponse result = messageService.publishMessage(userId, channelId, request);
        simpMessagingTemplate.convertAndSend(destination + request.getParentId(), result);
    }

    @PostMapping("/reply/file/{channelId}")
    public void publishFile(@RequestUser Long userId,
                            @PathVariable Long channelId,
                            @RequestParam MessageType type,
                            @ModelAttribute FileUploadRequest request) {
        MessageResponse result = messageService.publishFile(userId, channelId, type, request);
        simpMessagingTemplate.convertAndSend(destination + request.getParentId(), result);
    }

    @MessageMapping("/reply/typing/{parentId}")
    public void publishTypedUser(@Header("userId") Long userId, @DestinationVariable String parentId) {
        TypedUserResponse result = messageService.publishTypedUser(userId);
        simpMessagingTemplate.convertAndSend(destination + parentId, result);
    }

    @GetMapping("/replies/{channelId}")
    public ResponseEntity<Object> getParentByChannelId(@RequestUser Long userId,
                                                       @PathVariable Long channelId) {
        List<MessageResponse> result = messageService.getParentByChannelId(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "채널의 Reply 리스트 조회", result);
    }

    @GetMapping("/children")
    public ResponseEntity<Object> getRepliesByParentId(@RequestUser Long userId,
                                                       @RequestParam String parentId) {
        PagedMessageResponse result = messageService.getRepliesByParentId(userId, parentId);
        return ResultResponse.success(HttpStatus.OK, "부모 메시지의 Reply 리스트 조회", result);
    }

}
