package distove.auth.controller;

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

    /**
     * 유저 정보(id, nickname, profileImgUrl) 리턴
     */
    @GetMapping("/user")
    public UserResponse getUser(@RequestHeader Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * 유저 정보(id, nickname, profileImgUrl) 리스트 리턴
     */
    @GetMapping("/user/list")
    public List<UserResponse> getUsers(@RequestParam List<Long> userIds) {
        return userService.getUsersById(userIds);
    }

}
