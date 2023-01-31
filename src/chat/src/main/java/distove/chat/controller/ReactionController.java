package distove.chat.controller;

import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.ResultResponse;
import distove.chat.entity.Emoji;
import distove.chat.service.EmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReactionController {

    private final EmojiService emojiService;
    @GetMapping("/reaction/emoji/list")
    public ResponseEntity<Object> getEmojis(@RequestHeader("userId") Long userId,
                                            @RequestParam(required = false,value = "serverId") Long serverId) {
        List<Emoji> emojis = emojiService.getEmojis();

        return ResultResponse.success(HttpStatus.OK, "Emoji 조회", emojis);
    }

}
