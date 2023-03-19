package distove.voice.client;

import distove.voice.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user", url = "http://localhost:9000")
public interface UserClient {

    @GetMapping("/auth/web/user")
    UserResponse getUser(@RequestHeader Long userId);

    @GetMapping("/auth/web/user/list")
    List<UserResponse> getUsers(@RequestParam String userIds);

}
