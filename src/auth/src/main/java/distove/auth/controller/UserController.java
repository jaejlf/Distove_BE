package distove.auth.controller;

import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.SignUpRequest;
import distove.auth.dto.response.ResultResponse;
import distove.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        return ResultResponse.success(
                HttpStatus.CREATED,
                "로그인 성공",
                userService.login(request)
        );
    }

    @GetMapping("/user-emails/{email}/exists")
    public ResponseEntity<Object> checkEmailDuplicate(@PathVariable String email) {
        return ResultResponse.success(
                HttpStatus.OK,
                "이메일 중복",
                userService.checkEmailDuplicate(email));
    }

    @GetMapping("/user-nicknames/{nickname}/exists")
    public ResponseEntity<Object> checkNicknameDuplicate(@PathVariable String nickname) {
        return ResultResponse.success(
                HttpStatus.OK,
                "닉네임 중복",
                userService.checkNicknameDuplicate(nickname));
    }
}