package distove.auth.controller;

import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.SignUpRequest;
import distove.auth.dto.response.TokenResponse;
import distove.auth.service.UserService;
import distove.common.ResultResponse;
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
    public TokenResponse login(@RequestBody LoginRequest request){
        return userService.login(request);
    }

    @GetMapping("/user-emails/{email}/exists")
    public ResponseEntity<Boolean> checkEmailDuplicate(@PathVariable String email){
        return ResponseEntity.ok(userService.checkEmailDuplicate(email));
    }

    @GetMapping("/user-nicknames/{nickname}/exists")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname){
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }
}