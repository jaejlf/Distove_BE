package distove.chat.controller;

import distove.chat.dto.request.ReactionRequest;
import distove.chat.dto.response.ReactionMessageResponse;
import distove.chat.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/reaction/{channelId}")
    public void reactMessage(@Header("userId") Long userId,
                             @DestinationVariable Long channelId,
                             @Payload ReactionRequest reactionRequest) {
        ReactionMessageResponse result = reactionService.reactMessage(reactionRequest, userId);
        simpMessagingTemplate.convertAndSend("/sub/" + channelId, result);
    }

}
