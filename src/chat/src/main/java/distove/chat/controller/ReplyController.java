package distove.chat.controller;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.request.ReplyRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.enumerate.MessageType;
import distove.chat.service.MessageService;
import distove.chat.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class ReplyController {

    private final ReplyService replyService;
    private final MessageService messageService;

    // TODO : Response 형식 논의 필요 (void or else?)
    @PostMapping("/reply/{parentId}")
    public void createReply(@RequestHeader("userId") Long userId,
                            @PathVariable String parentId,
                            @Valid @RequestBody ReplyRequest request) {
        replyService.createReply(userId, parentId, request);
    }

    // TODO : Pub & Sub path 논의 필요
    @MessageMapping("/reply/{channelId}")
    @SendTo("/sub/{channelId}")
    public MessageResponse publishMessage(@DestinationVariable Long channelId, @Payload MessageRequest request) {
        return messageService.publishMessage(channelId, request);
    }

    @PostMapping("/reply/file/{channelId}")
    public void publishFile(@PathVariable Long channelId,
                            @RequestParam MessageType type,
                            @ModelAttribute FileUploadRequest request) {
        messageService.publishFile(channelId, type, request);
    }

}
