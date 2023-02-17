package distove.auth.config;

import distove.auth.service.JwtProvider;
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

        log.info("인증 구현 전까지 무조건 pass <-----");
//        String token = request.getHeader("token");
//        jwtProvider.validToken(token);

        Long userId = Long.valueOf(request.getHeader("userId"));
//        Long userId = jwtProvider.getUserId(token);
        request.setAttribute("userId", userId);
        return true;
    }

}
