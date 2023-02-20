package distove.community.controller;

import distove.community.dto.response.CategoryInfoResponse;
import distove.community.service.WebService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebController {

    private final WebService webService;

    @GetMapping("/web/category/list")
    public List<CategoryInfoResponse> getCategoryIds(@RequestParam List<Long> channelIds) {
        return webService.getCategoryIds(channelIds);
    }

    @GetMapping("/web/category")
    public CategoryInfoResponse getCategoryId(@RequestParam Long channelId) {
        return webService.getCategoryId(channelId);
    }

}
