package distove.auth.controller;

import distove.auth.dto.reponse.TokenResponse;
import distove.auth.dto.request.SignUpRequest;
import distove.auth.entity.User;
import distove.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpRequest request) {
        userService.signUp(request);
    }

    @GetMapping("/login")
    public TokenResponse login(@RequestBody User user){
        return userService.login(user);
    }
}