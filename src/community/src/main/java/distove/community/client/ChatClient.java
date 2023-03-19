package distove.community.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "chat", url = "http://localhost:9001")
public interface ChatClient {

    @PostMapping("/web/connection/server/{serverId}")
    void createConnection(@PathVariable Long serverId,
                          @RequestParam Long channelId);

    @DeleteMapping("/web/clear/{channelId}")
    void clear(@PathVariable Long channelId);

    @DeleteMapping("/web/clear/list")
    void clearAll(@RequestParam String channelIds);

}
