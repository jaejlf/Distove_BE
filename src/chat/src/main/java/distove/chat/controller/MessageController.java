package distove.chat.controller;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.dto.response.TypedUserResponse;
import distove.chat.entity.Message;
import distove.chat.enumerate.MessageType;
import distove.chat.service.MessageService;
import distove.chat.web.UserClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @Value("${sub.destination}")
    private String destination;

    @MessageMapping("/chat/{channelId}")
    public void publishMessage(@Header("userId") Long userId,
                               @DestinationVariable Long channelId,
                               @Payload MessageRequest request) {
        MessageResponse result = messageService.publishMessage(userId, channelId, request);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    @PostMapping("/file/{channelId}")
    public void publishFile(@RequestHeader("userId") Long userId,
                            @PathVariable Long channelId,
                            @RequestParam MessageType type,
                            @ModelAttribute FileUploadRequest request) {
        MessageResponse result = messageService.publishFile(userId, channelId, type, request);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    @MessageMapping("/typing/{channelId}")
    public void publishTypedUser(@Header("userId") Long userId, @DestinationVariable Long channelId) {
        TypedUserResponse result = messageService.publishTypedUser(userId);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    @GetMapping("/list/{channelId}")
    public ResponseEntity<Object> getMessagesByChannelId(@RequestHeader("userId") Long userId,
                                                         @PathVariable Long channelId,
                                                         @RequestParam(required = false) Integer scroll,
                                                         @RequestParam(required = false) String cursorId) {
        PagedMessageResponse result = messageService.getMessagesByChannelId(userId, channelId, scroll, cursorId);
        return ResultResponse.success(HttpStatus.OK, "메시지 리스트 조회", result);
    }

    @PatchMapping("/unsubscribe/{channelId}")
    public ResponseEntity<Object> unsubscribeChannel(@RequestHeader("userId") Long userId,
                                                     @PathVariable Long channelId) {
        messageService.unsubscribeChannel(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "채널 구독 해제", null);
    }

    @PatchMapping("/read/all/{channelId}")
    public ResponseEntity<Object> readAllUnreadMessages(@RequestHeader("userId") Long userId,
                                                        @PathVariable Long channelId) {
        messageService.readAllUnreadMessages(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "안읽메 모두 읽음", null);
    }

    @GetMapping("finding/{channelId}")
    private ResponseEntity<Object> findMessages(@RequestHeader("userId") Long userId,
                                                         @PathVariable Long channelId,
                                                         @RequestParam String content,
                                                         @RequestParam (required = false) String searchType) {
        List<MessageResponse> messages = messageService.findMessages(searchType, channelId, content);
        return ResultResponse.success(HttpStatus.OK, "메시지 검색", messages);
    }

}