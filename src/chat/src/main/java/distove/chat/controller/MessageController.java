package distove.chat.controller;

import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/pub/{channelId}")
    public void publishMessage(@RequestHeader("userId") Long userId,
                               @PathVariable Long channelId,
                               @Payload MessageRequest request) {
        messageService.publishMessage(userId, channelId, request);
    }

    @MessageMapping("/typing/{channelId}")
    public void publishMessage(@RequestHeader("userId") Long userId,
                               @PathVariable Long channelId) {
        messageService.onTyping(userId, channelId);
    }

    @GetMapping("/list/{channelId}")
    public ResponseEntity<Object> getMessages(@RequestHeader("userId") Long userId,
                                              @PathVariable String channelId) {
        List<MessageResponse> result = messageService.getMessages(userId, Long.parseLong(channelId));
        return ResultResponse.success(HttpStatus.OK, "메시지 리스트 조회", result);
    }

}
