package distove.auth.controller;

import distove.auth.dto.request.*;
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

    @PostMapping("/join")
    public ResponseEntity<Object> join(@ModelAttribute JoinRequest request) {
        return ResultResponse.success(
                HttpStatus.CREATED,
                "회원가입",
                userService.join(request)
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
        log.info(token);
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
    public ResponseEntity<Object> updateUser(@RequestHeader("token") String token, @RequestBody UpdateNicknameRequest request) {
        return ResultResponse.success(
                HttpStatus.OK,
                "닉네임 수정 성공",
                userService.updateNickname(token, request)
        );
    }

    @PutMapping("/profileimage")
    public ResponseEntity<Object> updateProfileImage(@RequestHeader("token") String token, @ModelAttribute UpdateProfileImageRequest request) {
        return ResultResponse.success(
                HttpStatus.OK,
                "프로필 사진 수정 성공",
                userService.updateProfileImage(token, request)
        );
    }

    @GetMapping("/reissue")
    public ResponseEntity<Object> reissue(@RequestHeader("token") String token) {
        return ResultResponse.success(
                HttpStatus.CREATED,
                "토큰 재발급",
                userService.reissue(token)
        );
    }
}