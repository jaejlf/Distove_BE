package distove.chat.controller;

import distove.chat.config.RequestUser;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 특정 채널의 메시지 리스트 조회
     * * if 채널 최초 접속 시 -> 멤버 정보를 생성하고 WELCOME 메시지 publish
     * * if DEFAULT 스크롤일 경우 -> 전체 채널의 알림 publish
     *
     * @param scroll   : DEFAULT(-1), DOWN(0), UP(1)
     * @param cursorId : DOWN/UP 스크롤일 경우의 커서 메시지 id
     * @return : UnreadInfo, CursorInfo, MessageResponses
     */
    @GetMapping("/list/{channelId}")
    public ResponseEntity<Object> getMessagesByChannelId(@RequestUser Long userId,
                                                         @PathVariable Long channelId,
                                                         @RequestParam(required = false) Integer scroll,
                                                         @RequestParam(required = false) String cursorId) {

//        if (scroll == null) notificationService.publishAllNotification(userId, connection.getServerId()); // 안읽메 알림 PUSH
        PagedMessageResponse result = messageService.getMessagesByChannelId(userId, channelId, scroll, cursorId);
        return ResultResponse.success(HttpStatus.OK, "채널의 메시지 리스트 조회", result);
    }

    /**
     * 특정 메시지의 스레드 메시지 리스트 조회
     *
     * @param messageId : 부모 메시지 id
     */
    @GetMapping("/message/{messageId}/threads")
    public ResponseEntity<Object> getRepliesByParentId(@RequestUser Long userId,
                                                       @PathVariable String messageId) {
        PagedMessageResponse result = messageService.getThreadsByMessageId(userId, messageId);
        return ResultResponse.success(HttpStatus.OK, "메시지의 스레드 메시지 리스트 조회", result);
    }

    /**
     * 특정 채널의 스레드 메시지 리스트 조회
     */
    @GetMapping("/channel/{channelId}/threads")
    public ResponseEntity<Object> getParentByChannelId(@RequestUser Long userId,
                                                       @PathVariable Long channelId) {
        List<MessageResponse> result = messageService.getThreadsByChannelId(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "채널의 스레드 메시지 리스트 조회", result);
    }

    /**
     * 특정 채널에 존재하는 '안 읽은 메시지' 모두 읽음 처리
     */
    @PatchMapping("/read/all/{channelId}")
    public ResponseEntity<Object> readAllUnreadMessages(@RequestUser Long userId,
                                                        @PathVariable Long channelId) {
        messageService.readAllMessages(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "안읽메 모두 읽음", null);
    }

//    @PatchMapping("/unsubscribe/{channelId}")
//    public ResponseEntity<Object> unsubscribeChannel(@RequestUser Long userId,
//                                                     @PathVariable Long channelId) {
//        messageService.unsubscribeChannel(userId, channelId);
//        return ResultResponse.success(HttpStatus.OK, "채널 구독 해제", null);
//    }

}