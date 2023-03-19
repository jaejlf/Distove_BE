package distove.presence.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(value = "community", url = "http://localhost:9002")
public interface CommunityClient {

    @GetMapping("/community/web/server/ids")
    List<Long> getServerIdsByUserId(@RequestHeader("userId") Long userId);

    @GetMapping("/community/web/server/{serverId}/users")
    List<UserResponse> getUsersByServerId(@PathVariable("serverId") Long serverId);

}
