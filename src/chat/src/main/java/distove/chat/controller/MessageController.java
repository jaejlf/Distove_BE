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
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat/{channelId}")
    @SendTo("/sub/{channelId}")
    public MessageResponse publishMessage(@DestinationVariable Long channelId, @Payload MessageRequest request) {
        return messageService.publishMessage(channelId, request);
    }

    @PostMapping("/file/{channelId}")
    public void publishFile(@PathVariable Long channelId,
                            @RequestParam MessageType type,
                            @ModelAttribute FileUploadRequest request) {
        MessageResponse result = messageService.publishFile(channelId, type, request);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, result);
    }

    @MessageMapping("/typing/{channelId}")
    @SendTo("/sub/{channelId}")
    public TypedUserResponse beingTyped(@Payload MessageRequest request) {
        Long userId = request.getUserId();
        return messageService.beingTyped(userId);
    }

    @GetMapping("/list/{channelId}")
    public ResponseEntity<Object> getMessages(@RequestHeader("userId") Long userId,
                                              @PathVariable Long channelId) {
        List<MessageResponse> result = messageService.getMessages(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "메시지 리스트 조회", result);
    }

}
