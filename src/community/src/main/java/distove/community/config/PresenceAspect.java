package distove.community.config;

import distove.community.enumerate.ServiceInfoType;
import distove.community.web.PresenceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class PresenceAspect {

    private final PresenceClient presenceClient;

    @Pointcut(value="execution(* distove.community.controller.ChannelController.*(..))" +
            "|| execution(* distove.community.controller.MemberController.*(..)) " +
            "|| execution(* distove.community.controller.ServerController.*(..))")
    public void pointUpdatePresence(){};

    @Pointcut(value="execution(* distove.community.controller.*.*(..))")
    public void pointTest(){};


    @Before("pointUpdatePresence()")
    public void updatePresence(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Long userId = (Long) request.getAttribute("userId");
        presenceClient.updateUserPresence(userId, ServiceInfoType.COMMUNITY.getType());

    }


}