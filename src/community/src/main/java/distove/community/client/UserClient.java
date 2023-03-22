package distove.community.client;

import distove.community.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user", url = "http://localhost:9000")
public interface UserClient {

    @GetMapping("/auth/web/user")
    UserResponse getUser(@RequestHeader("userId") Long userId);

    @GetMapping("/auth/web/user/list")
    List<UserResponse> getUsers(@RequestParam("userIds") String userIdsString);

    @GetMapping("/auth/web/user/nickname")
    List<Long> getUserByNickname(@RequestParam String nickname);

}