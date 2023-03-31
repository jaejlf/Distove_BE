package distove.chat.controller;

import distove.chat.config.RequestUser;
import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.TypingUserResponse;
import distove.chat.service.ChatService;
import distove.chat.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final StorageService storageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${sub.destination}")
    private String destination;

    /**
     * 메시지 발행
     *
     * @param request * MessageType : TEXT
     *                * MessageStatus : CREATED, MODIFIED, DELETED, REACTED
     */
    @MessageMapping("/chat/message/{channelId}")
    public void publishMessage(@Header("userId") Long userId,
                               @DestinationVariable Long channelId,
                               @Valid @Payload MessageRequest request) {
        MessageResponse result = chatService.publishMessage(userId, channelId, request);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    /**
     * 파일 타입 메시지 발행
     *
     * @param request * MessageType : IMAGE, FILE, VIDEO
     *                * MessageStatus : CREATED
     */
    @PostMapping("/chat/file/{channelId}")
    public void publishFile(@RequestUser Long userId,
                            @PathVariable Long channelId,
                            @Valid @ModelAttribute FileUploadRequest request) {
        String content = storageService.uploadToS3(request.getFile(), request.getType());
        MessageRequest messageRequest = MessageRequest.ofFileType(request.getType(), content, request.getParentId());
        MessageResponse result = chatService.publishMessage(userId, channelId, messageRequest);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    /**
     * 일반 채널에서 '작성 중'인 유저
     */
    @MessageMapping("/chat/typing/{channelId}")
    public void publishTypedUser(@Header("userId") Long userId, @DestinationVariable Long channelId) {
        TypingUserResponse result = chatService.publishTypingUser(userId);
        simpMessagingTemplate.convertAndSend(destination + channelId, result);
    }

    /**
     * 스레드 메시지 발행
     *
     * @param request * MessageType : TEXT
     *                * MessageStatus : CREATED, MODIFIED, DELETED, REACTED
     */
    @MessageMapping("/thread/message/{channelId}")
    public void publishThreadMessage(@Header("userId") Long userId,
                                     @DestinationVariable Long channelId,
                                     @Payload MessageRequest request) {
        if (request.getThreadName() != null) chatService.createThread(userId, channelId, request); // 최초 스레드 시작
        MessageResponse result = chatService.publishMessage(userId, channelId, request);
        simpMessagingTemplate.convertAndSend(destination + request.getParentId(), result);
    }

    /**
     * 파일 타입 스레드 메시지 발행
     *
     * @param request * MessageType : IMAGE, FILE, VIDEO
     *                * MessageStatus : CREATED
     */
    @PostMapping("/thread/file/{channelId}")
    public void publishThreadFile(@RequestUser Long userId,
                                  @PathVariable Long channelId,
                                  @ModelAttribute FileUploadRequest request) {
        String content = storageService.uploadToS3(request.getFile(), request.getType());
        MessageRequest messageRequest = MessageRequest.ofFileType(request.getType(), content, request.getParentId());
        MessageResponse result = chatService.publishMessage(userId, channelId, messageRequest);
        simpMessagingTemplate.convertAndSend(destination + request.getParentId(), result);
    }

    /**
     * 스레드 채널에서 '작성 중'인 유저
     */
    @MessageMapping("/thread/typing/{parentId}")
    public void publishThreadTypingUser(@Header("userId") Long userId, @DestinationVariable String parentId) {
        TypingUserResponse result = chatService.publishTypingUser(userId);
        simpMessagingTemplate.convertAndSend(destination + parentId, result);
    }

}
