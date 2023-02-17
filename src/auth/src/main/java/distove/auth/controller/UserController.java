package distove.auth.controller;

import distove.auth.dto.request.JoinRequest;
import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.UpdateNicknameRequest;
import distove.auth.dto.request.UpdateProfileImgRequest;
import distove.auth.dto.response.ResultResponse;
import distove.auth.dto.response.LoginResponse;
import distove.auth.dto.response.UserResponse;
import distove.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<Object> join(@Valid @ModelAttribute JoinRequest request) {
        UserResponse result = userService.join(request);
        return ResultResponse.success(
                HttpStatus.CREATED,
                "회원가입",
                result
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = userService.login(request);
        response.addHeader("Set-Cookie", userService.createCookie(request));
        return ResultResponse.success(
                HttpStatus.CREATED,
                "로그인",
                loginResponse
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("token") String token) {
        UserResponse result = userService.logout(token);
        return ResultResponse.success(
                HttpStatus.OK,
                "로그아웃 성공",
                result
        );
    }

    @PutMapping("/nickname")
    public ResponseEntity<Object> updateUser(@RequestHeader("token") String token, @RequestBody UpdateNicknameRequest request) {
        UserResponse result = userService.updateNickname(token, request);
        return ResultResponse.success(
                HttpStatus.OK,
                "닉네임 수정 성공",
                result
        );
    }

    @PutMapping("/profileimg")
    public ResponseEntity<Object> updateProfileImg(@RequestHeader("token") String token, @ModelAttribute UpdateProfileImgRequest request) {
        UserResponse result = userService.updateProfileImg(token, request);
        return ResultResponse.success(
                HttpStatus.OK,
                "프로필 사진 수정 성공",
                result
        );
    }

    @GetMapping("/reissue")
    public ResponseEntity<Object> reissue(HttpServletResponse response, HttpServletRequest request) {
        LoginResponse loginResponse = userService.reissue(request);
        response.addHeader("Set-Cookie", userService.createCookieFromReissue(request));
        return ResultResponse.success(
                HttpStatus.CREATED,
                "액세스 토큰 재발급",
                loginResponse
        );
    }
}