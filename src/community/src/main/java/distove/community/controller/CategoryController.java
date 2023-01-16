package distove.community.controller;

import distove.common.ResultResponse;
import distove.community.dto.response.CategoryResponse;
import distove.community.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category")
    public ResponseEntity<Object> getServersByUserId(@RequestHeader("serverId") Long serverId){
        List<CategoryResponse> categories = categoryService.getCategoriesByServerId(serverId);
        return ResultResponse.success(HttpStatus.OK,"그룹의 채널 및 카테고리 조회",categories);
    }

}
