package distove.chat.controller;

import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.service.MessageService;
import distove.common.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/pub/{channelId}")
    public void publishMessage(@RequestHeader("userId") Long userId,
                               @PathVariable Long channelId,
                               @Payload MessageRequest request) throws ExecutionException, InterruptedException {
        messageService.publishMessage(userId, channelId, request);
    }

    @GetMapping("/list/{channelId}")
    public ResponseEntity<Object> getMessages(@RequestHeader("userId") Long userId,
                                              @PathVariable String channelId) {
        List<MessageResponse> result = messageService.getMessages(userId, Long.parseLong(channelId));
        return ResultResponse.success(HttpStatus.OK, "메시지 리스트 조회", result);
    }

}
