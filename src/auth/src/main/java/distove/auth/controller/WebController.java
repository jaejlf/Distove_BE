package distove.auth.controller;

import distove.auth.dto.response.UserResponse;
import distove.auth.web.GetUserFromToken;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebController {

    private final GetUserFromToken getUserFromToken;

    @GetMapping("/user")
    public UserResponse getUser(@RequestHeader("userId") String token) {
        return getUserFromToken.getUser(token);
    }
}
