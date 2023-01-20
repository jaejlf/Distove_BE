package distove.auth.controller;

import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.LogoutRequest;
import distove.auth.dto.request.SignUpRequest;
import distove.auth.dto.response.LogoutResponse;
import distove.auth.dto.response.ResultResponse;
import distove.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody SignUpRequest request) {
        return ResultResponse.success(
                HttpStatus.CREATED,
                "회원가입",
                userService.signUp(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        return ResultResponse.success(
                HttpStatus.CREATED,
                "로그인 성공",
                userService.login(request)
        );
    }

    @GetMapping("/signout")
    public ResponseEntity<Object> logout(@RequestBody LogoutRequest request) {
        LogoutResponse logoutResponse = userService.signOut(request);
        return ResultResponse.success(
                HttpStatus.OK,
                "로그아웃 성공",
                logoutResponse
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> checkEmailDuplicate(@PathVariable String email) {
        return ResultResponse.success(
                HttpStatus.OK,
                "이메일 중복",
                userService.checkEmailDuplicate(email));
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Object> checkNicknameDuplicate(@PathVariable String nickname) {
        return ResultResponse.success(
                HttpStatus.OK,
                "닉네임 중복",
                userService.checkNicknameDuplicate(nickname));
    }
}