package distove.chat.controller;

import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.entity.Message;
import distove.chat.service.MessageService;
import distove.chat.web.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat/{channelId}")
    @SendTo("/sub/{channelId}")
    public MessageResponse publishMessage(@DestinationVariable Long channelId,
                                          @Payload MessageRequest request) {
        UserResponse writer = new UserResponse(1L, "aaa", "bbbb");
        Message message = new Message(
                channelId,
                writer.getId(),
                request.getType(),
                request.getContent()
        );
        return MessageResponse.of(message, writer, 1L);
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
