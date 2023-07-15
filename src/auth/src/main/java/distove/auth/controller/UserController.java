package distove.auth.controller;

import distove.auth.config.RequestUser;
import distove.auth.dto.request.JoinRequest;
import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.response.LoginResponse;
import distove.auth.dto.response.ResultResponse;
import distove.auth.dto.response.UserResponse;
import distove.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<Object> join(@Valid @ModelAttribute JoinRequest request) {
        UserResponse result = userService.join(request);
        return ResultResponse.success(HttpStatus.CREATED, "회원가입", result);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse result = userService.login(request);
        Long userId = result.getUser().getId();
        response.addHeader("Set-Cookie", userService.createCookie(userId));
        return ResultResponse.success(HttpStatus.CREATED, "로그인", result);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestUser Long userId) {
        UserResponse result = userService.logout(userId);
        return ResultResponse.success(HttpStatus.OK, "로그아웃", result);
    }

    /**
     * 토큰 재발급
     *
     * @throws 401 리프레쉬 토큰 만료
     * @throws 403 잘못된 리프레쉬 토큰
     */
    @PostMapping("/reissue")
    public ResponseEntity<Object> reissue(HttpServletRequest request, HttpServletResponse response) {
        LoginResponse result = userService.reissue(request);
        Long userId = result.getUser().getId();
        response.addHeader("Set-Cookie", userService.createCookie(userId));
        return ResultResponse.success(HttpStatus.CREATED, "토큰 재발급", result);
    }

}
