package distove.chat.aspect;

import distove.chat.dto.request.MessageRequest;
import distove.chat.util.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static distove.chat.enumerate.MessageType.validateTypeAndStatus;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ValidationAspect {

    private final MemberValidator memberValidator;

    @Pointcut("execution(* distove.chat.service.ChatService.publishMessage())")
    public void publishMessageAspect() {
    }

    @Pointcut("execution(* distove.chat.service.MessageService.getMessagesByChannelId())")
    public void getMessagesByChannelIdAspect() {
    }

    @Before("publishMessageAspect() && args(userId,channelId,request)")
    public void validateMemberForPublishMessage(Long userId, Long channelId, MessageRequest request) {
        memberValidator.validateMember(userId, channelId);
        validateTypeAndStatus(request.getType(), request.getStatus());
    }

    @Before("getMessagesByChannelIdAspect() && args(userId,channelId)")
    public void validateMemberForGetMessages(Long userId, Long channelId) {
        memberValidator.validateMember(userId, channelId);
    }

}
