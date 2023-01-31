package distove.auth.controller;

import distove.auth.dto.request.EmailDuplicateRequest;
import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.UpdateNicknameRequest;
import distove.auth.dto.request.SignUpRequest;
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
    public ResponseEntity<Object> signUp(@ModelAttribute SignUpRequest request) {
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

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("token") String token) {
        return ResultResponse.success(
                HttpStatus.OK,
                "로그아웃 성공",
                userService.logout(token)
        );
    }

    @PostMapping("/email")
    public ResponseEntity<Object> checkEmailDuplicate(@RequestBody EmailDuplicateRequest request) {
        return ResultResponse.success(
                HttpStatus.OK,
                "이메일 사용 여부",
                userService.checkEmailDuplicate(request)
        );
    }

    @PutMapping("/nickname")
    public ResponseEntity<Object> updateUser(@RequestBody UpdateNicknameRequest request) {
        return ResultResponse.success(
                HttpStatus.OK,
                "수정 성공",
                userService.updateNickname(request)
        );
    }

}