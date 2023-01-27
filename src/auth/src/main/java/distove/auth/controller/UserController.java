package distove.auth.controller;

import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.SignUpRequest;
import distove.auth.dto.response.ResultResponse;
import distove.auth.repoisitory.UserRepository;
import distove.auth.service.StorageService;
import distove.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    private final UserService userService;
    private final StorageService storageService;


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

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("AT") String token) {
        return ResultResponse.success(
                HttpStatus.OK,
                "로그아웃 성공",
                userService.logout(token)
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> checkEmailDuplicate(@PathVariable String email) {
        return ResultResponse.success(
                HttpStatus.OK,
                "이메일 중복",
                userService.checkEmailDuplicate(email));
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadProfileImg(@RequestPart("file") MultipartFile file) {
        return ResultResponse.success(
                HttpStatus.OK,
                "이미지 업로드 성공",
                storageService.uploadToS3(file));
    }
}
