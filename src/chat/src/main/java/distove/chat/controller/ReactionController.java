package distove.chat.controller;

import distove.chat.dto.request.ReactionRequest;
import distove.chat.dto.response.ReactionMessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.entity.Emoji;
import distove.chat.service.EmojiService;
import distove.chat.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReactionController {

    private final EmojiService emojiService;
    private final ReactionService reactionService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/reaction/emoji/list")
    public ResponseEntity<Object> getEmojis(@RequestHeader("userId") Long userId,
                                            @RequestParam(required = false, value = "serverId") Long serverId) {
        List<Emoji> emojis = emojiService.getEmojis();

        return ResultResponse.success(HttpStatus.OK, "Emoji 조회", emojis);
    }

    @MessageMapping("/reaction/{channelId}")
    public void reactMessage(@Header("userId") Long userId,
                             @DestinationVariable Long channelId,
                             @Payload ReactionRequest reactionRequest) {
        ReactionMessageResponse result = reactionService.reactMessage(reactionRequest, userId);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, result);
    }

}
