package distove.chat.controller;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.enumerate.MessageType;
import distove.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat/{channelId}")
    public void publishMessage(@DestinationVariable Long channelId, @Payload MessageRequest request) {
        MessageResponse result = messageService.publishMessage(channelId, request);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, result);
    }

    @PostMapping("/file/{channelId}")
    public void publishFile(@PathVariable Long channelId,
                            @RequestParam MessageType type,
                            @ModelAttribute FileUploadRequest request) {
        MessageResponse result = messageService.publishFile(channelId, type, request);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, result);
    }

    @MessageMapping("/typing/{channelId}")
    public void publishTypedUser(@DestinationVariable Long channelId, @Payload MessageRequest request) {
        Long userId = request.getUserId();
        TypedUserResponse result = messageService.publishTypedUser(userId);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, result);
    }

    @GetMapping("/list/{channelId}")
    public ResponseEntity<Object> getMessagesByChannelId(@RequestHeader("userId") Long userId,
                                                         @PathVariable Long channelId) {
        List<MessageResponse> result = messageService.getMessagesByChannelId(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "메시지 리스트 조회", result);
    }

}
