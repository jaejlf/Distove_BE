package distove.chat.controller;

import distove.chat.config.RequestUser;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.service.ConnectionService;
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
    private final ConnectionService connectionService;

    @GetMapping("/list/{channelId}")
    public ResponseEntity<Object> getMessagesByChannelId(@RequestUser Long userId,
                                                         @PathVariable Long channelId,
                                                         @RequestParam(required = false) Integer scroll,
                                                         @RequestParam(required = false) String cursorId) {

        PagedMessageResponse result = messageService.getMessagesByChannelId(userId, channelId, scroll, cursorId);
        return ResultResponse.success(HttpStatus.OK, "메시지 리스트 조회", result);
    }

    @PatchMapping("/unsubscribe/{channelId}")
    public ResponseEntity<Object> unsubscribeChannel(@RequestUser Long userId,
                                                     @PathVariable Long channelId) {
        messageService.unsubscribeChannel(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "채널 구독 해제", null);
    }

    @PatchMapping("/read/all/{channelId}")
    public ResponseEntity<Object> readAllUnreadMessages(@RequestUser Long userId,
                                                        @PathVariable Long channelId) {
        messageService.readAllUnreadMessages(userId, channelId);
        return ResultResponse.success(HttpStatus.OK, "안읽메 모두 읽음", null);
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