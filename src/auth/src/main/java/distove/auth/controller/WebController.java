package distove.auth.controller;

import distove.auth.dto.response.UserResponse;
import distove.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;

    @GetMapping("/user")
    public UserResponse getUser(@RequestHeader Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/user/list")
    public List<UserResponse> getUsers(@RequestBody List<Long> userIds) {
        return userService.getUsers(userIds);
    }
}
