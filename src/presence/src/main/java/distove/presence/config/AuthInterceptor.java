package distove.presence.config;

import distove.presence.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(request.getMethod().equals("OPTIONS")) return true;

        log.info("-----> AuthCheckInterceptor 진입");

        String token = request.getHeader("token");
        jwtProvider.validToken(token);

        Long userId = jwtProvider.getUserId(token);
        request.setAttribute("userId", userId);
        return true;
    }

}
