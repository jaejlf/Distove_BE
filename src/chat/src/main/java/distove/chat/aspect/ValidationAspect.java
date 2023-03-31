package distove.chat.aspect;

import distove.chat.dto.request.MessageRequest;
import distove.chat.service.MessageService;
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
    private final MessageService messageService;

    @Pointcut("execution(* distove.chat.service.ChatService.publishMessage())")
    public void publishMessageAspect() {
    }

    @Pointcut("execution(* distove.chat.service.MessageService.getMessagesByChannelId())")
    public void getMessagesByChannelIdAspect() {
    }

    @Pointcut("execution(* distove.chat.service.MessageService.getThreadsByMessageId())")
    public void getThreadsByMessageIdAspect() {
    }

    @Pointcut("execution(* distove.chat.service.MessageService.getThreadsByChannelId())")
    public void getThreadsByChannelIdAspect() {
    }

    /**
     * @when 메시지 발행 시
     * @then 멤버 & 메시지 유효성 검사
     */
    @Before("publishMessageAspect() && args(userId,channelId,request)")
    public void validateMemberForPublishMessage(Long userId, Long channelId, MessageRequest request) {
        memberValidator.validateMember(userId, channelId);
        validateTypeAndStatus(request.getType(), request.getStatus());
    }

    /**
     * @when 특정 채널의 메시지 리스트 조회 시
     * @then 멤버 유효성 검사
     */
    @Before("getMessagesByChannelIdAspect() && args(userId,channelId)")
    public void validateMemberForGetMessages(Long userId, Long channelId) {
        memberValidator.validateMember(userId, channelId);
    }

    /**
     * @when 메시지의 스레드 메시지 리스트 조회 시
     * @then 멤버 유효성 검사
     */
    @Before("getThreadsByMessageIdAspect() && args(userId,messageId)")
    public void validateMemberForGetThreadsByMessageId(Long userId, String messageId) {
        Long channelId = messageService.getMessage(messageId).getChannelId();
        memberValidator.validateMember(userId, channelId);
    }

    /**
     * @when 채널의 스레드 메시지 리스트 조회 시
     * @then 멤버 유효성 검사
     */
    @Before("getThreadsByChannelIdAspect() && args(userId,channelId)")
    public void validateMemberForGetThreadsByChannelId(Long userId, Long channelId) {
        memberValidator.validateMember(userId, channelId);
    }

}
