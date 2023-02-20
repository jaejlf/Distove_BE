package distove.community.config;

import distove.community.entity.Member;
import distove.community.exception.DistoveException;
import distove.community.repository.MemberRepository;
import distove.community.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static distove.community.exception.ErrorCode.MEMBER_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberAuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(request.getMethod().equals("OPTIONS")) return true;

        log.info("-----> AuthCheckInterceptor 진입");

        String token = request.getHeader("token");
        jwtProvider.validToken(token);

        Long userId = jwtProvider.getUserId(token);
        request.setAttribute("userId", userId);

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AuthorizedRole authorizedRole = handlerMethod.getMethodAnnotation(AuthorizedRole.class);
        if (authorizedRole != null) checkHasAuthorizedRole(request, userId, authorizedRole);
        return true;
    }

    private void checkHasAuthorizedRole(HttpServletRequest request, Long userId, AuthorizedRole authorizedRole) {
        log.info("-----> Authorized Role 체크");
        log.info("역할 구현 전까지 무조건 pass <-----");
//        Member member;
//        Auth auth = authorizedRole.name();
//        switch (auth) {
//            case CAN_DELETE_SERVER:
//                member = getMemberByPath(request, userId);
//                if (!member.getRole().isCanDeleteServer()) throw new DistoveException(NO_AUTH_ERROR);
//            case CAN_MANAGE_SERVER:
//                member = getMemberByPath(request, userId);
//                if (!member.getRole().isCanManageServer()) throw new DistoveException(NO_AUTH_ERROR);
//            case CAN_MANAGE_CHANNEL:
//                member = getMemberByQuery(request, userId);
//                if (!member.getRole().isCanManageChannel()) throw new DistoveException(NO_AUTH_ERROR);
//            case CAN_UPDATE_MEMBER_ROLE:
//                member = getMemberByPath(request, userId);
//                if (!member.getRole().isCanUpdateMemberRole()) throw new DistoveException(NO_AUTH_ERROR);
//        }
    }

    private Member getMemberByPath(HttpServletRequest request, Long userId) {
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String serverId = pathVariables.get("serverId").toString();
        return memberRepository.findByUserIdAndServerId(userId, Long.valueOf(serverId))
                .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND));
    }

    private Member getMemberByQuery(HttpServletRequest request, Long userId) {
        String serverId = request.getParameter("serverId");
        return memberRepository.findByUserIdAndServerId(userId, Long.valueOf(serverId))
                .orElseThrow(() -> new DistoveException(MEMBER_NOT_FOUND));
    }

}
