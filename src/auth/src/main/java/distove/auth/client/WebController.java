package distove.auth.client;

import distove.auth.dto.response.UserResponse;
import distove.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/web")
public class WebController {

    private final UserService userService;

    @GetMapping("/user")
    public UserResponse getUser(@RequestHeader Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/user/list")
    public List<UserResponse> getUsers(@RequestParam List<Long> userIds) {
        return userService.getUsers(userIds);
    }

}
