package distove.community.interceptor;

import distove.community.config.AuthorizedRole;
import distove.community.entity.Member;
import distove.community.exception.DistoveException;
import distove.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static distove.community.config.AuthorizedRole.Auth;
import static distove.community.exception.ErrorCode.MEMBER_NOT_FOUND_ERROR;
import static distove.community.exception.ErrorCode.NO_AUTH_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberAuthInterceptor implements HandlerInterceptor {

    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AuthorizedRole authorizedRole = handlerMethod.getMethodAnnotation(AuthorizedRole.class);

        if (authorizedRole != null) {
            Long userId = Long.valueOf(request.getHeader("userId")); // 토큰에서 user Id 꺼내기

            Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String serverId = pathVariables.get("serverId").toString();

            Member member = memberRepository.findByUserIdAndServerId(userId, Long.valueOf(serverId))
                    .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND_ERROR));

            Auth auth = authorizedRole.name();
            switch (auth) {
                case CAN_DELETE_SERVER:
                    if (!member.getRole().isCanDeleteServer()) throw new DistoveException(NO_AUTH_ERROR);
                case CAN_MANAGE_SERVER:
                    if (!member.getRole().isCanManageServer()) throw new DistoveException(NO_AUTH_ERROR);
                case CAN_MANAGE_CHANNEL:
                    if (!member.getRole().isCanManageChannel()) throw new DistoveException(NO_AUTH_ERROR);
                case CAN_UPDATE_MEMBER_ROLE:
                    if (!member.getRole().isCanUpdateMemberRole()) throw new DistoveException(NO_AUTH_ERROR);
            }
        }

        return true;

    }

}
