package distove.chat.controller;

import distove.chat.config.RequestUser;
import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.request.ReactionRequest;
import distove.chat.dto.response.*;
import distove.chat.enumerate.MessageType;
import distove.chat.factory.PublishFactory;
import distove.chat.service.ChatService;
import distove.chat.service.MessageService;
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

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;
    private final PublishFactory publishFactory;

    @Value("${sub.destination}")
    private String destination;

    @MessageMapping("/chat/{channelId}")
    public void publishReply(@Header("userId") Long userId,
                               @DestinationVariable Long channelId,
                               @Payload MessageRequest request) {

        // 팩토리 적용 버전
        MessageService messageService = publishFactory.getServiceByStatus(request.getStatus());
        messageService.publishMessage(userId, channelId, request);

        MessageResponse result = chatService.publishMessage(userId, channelId, request);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    @PostMapping("/file/{channelId}")
    public void publishReplyFile(@RequestUser Long userId,
                            @PathVariable Long channelId,
                            @RequestParam MessageType type,
                            @ModelAttribute FileUploadRequest request) {
        MessageResponse result = chatService.publishFile(userId, channelId, type, request);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    @MessageMapping("/typing/{channelId}")
    public void publishTypedUser(@Header("userId") Long userId, @DestinationVariable Long channelId) {
        TypedUserResponse result = chatService.publishTypedUser(userId);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    @GetMapping("/list/{channelId}")
    public ResponseEntity<Object> getMessagesByChannelId(@RequestUser Long userId,
                                                         @PathVariable Long channelId,
                                                         @RequestParam(required = false) Integer scroll,
                                                         @RequestParam(required = false) String cursorId) {
        PagedMessageResponse result = chatService.getMessagesByChannelId(userId, channelId, scroll, cursorId);
        return ResultResponse.success(HttpStatus.OK, "메시지 리스트 조회", result);
    }

    @PatchMapping("/unsubscribe/{channelId}")
    public ResponseEntity<Object> unsubscribeChannel(@RequestUser Long userId,
                                                     @PathVariable Long channelId) {
        chatService.unsubscribeChannel(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "채널 구독 해제", null);
    }

    @PatchMapping("/read/all/{channelId}")
    public ResponseEntity<Object> readAllUnreadMessages(@RequestUser Long userId,
                                                        @PathVariable Long channelId) {
        chatService.readAllUnreadMessages(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "안읽메 모두 읽음", null);
    }

    @MessageMapping("/reply/{channelId}")
    public void publishMessage(@Header("userId") Long userId,
                               @DestinationVariable Long channelId,
                               @Payload MessageRequest request) {
        if (request.getReplyName() != null) {
            simpMessagingTemplate.convertAndSend(
                    destination + channelId,
                    chatService.createReply(userId, request));
        }

        MessageResponse result = chatService.publishMessage(userId, channelId, request);
        simpMessagingTemplate.convertAndSend(destination + request.getParentId(), result);
    }

    @PostMapping("/reply/file/{channelId}")
    public void publishFile(@RequestUser Long userId,
                            @PathVariable Long channelId,
                            @RequestParam MessageType type,
                            @ModelAttribute FileUploadRequest request) {
        MessageResponse result = chatService.publishFile(userId, channelId, type, request);
        simpMessagingTemplate.convertAndSend(destination + request.getParentId(), result);
    }

    @MessageMapping("/reply/typing/{parentId}")
    public void publishTypedUser(@Header("userId") Long userId, @DestinationVariable String parentId) {
        TypedUserResponse result = chatService.publishTypedUser(userId);
        simpMessagingTemplate.convertAndSend(destination + parentId, result);
    }

    @GetMapping("/replies/{channelId}")
    public ResponseEntity<Object> getParentByChannelId(@RequestUser Long userId,
                                                       @PathVariable Long channelId) {
        List<MessageResponse> result = chatService.getParentByChannelId(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "채널의 Reply 리스트 조회", result);
    }

    @GetMapping("/children")
    public ResponseEntity<Object> getRepliesByParentId(@RequestUser Long userId,
                                                       @RequestParam String parentId) {
        PagedMessageResponse result = chatService.getRepliesByParentId(userId, parentId);
        return ResultResponse.success(HttpStatus.OK, "부모 메시지의 Reply 리스트 조회", result);
    }

    @MessageMapping("/reaction/{channelId}")
    public void reactMessage(@Header("userId") Long userId,
                             @DestinationVariable Long channelId,
                             @Payload ReactionRequest reactionRequest) {
        ReactionMessageResponse result = chatService.reactMessage(reactionRequest, userId);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

}
