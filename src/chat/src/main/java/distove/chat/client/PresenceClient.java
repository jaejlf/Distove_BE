package distove.chat.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "presence", url = "http://localhost:9004")
public interface PresenceClient {

    @PostMapping("/presence/web/update")
    void updateUserPresence(@RequestHeader Long userId,
                            @RequestParam String presenceType);

}
