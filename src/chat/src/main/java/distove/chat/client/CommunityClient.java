package distove.chat.client;

import distove.chat.client.dto.CategoryInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "community", url = "http://localhost:9002")
public interface CommunityClient {

    @GetMapping("/community/web/category/list")
    List<CategoryInfoResponse> getCategoryIds(@RequestParam String channelIdsString);

    @GetMapping("/community/web/category")
    CategoryInfoResponse getCategoryId(@RequestParam Long channelId);

    @GetMapping("/community/web/channel/{channelId}/member")
    boolean isMember(@PathVariable Long channelId, @RequestParam Long userId);

}
