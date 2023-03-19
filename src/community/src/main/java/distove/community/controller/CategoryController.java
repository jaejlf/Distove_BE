package distove.community.controller;

import distove.community.config.AuthorizedRole;
import distove.community.dto.request.NewCategoryRequest;
import distove.community.dto.request.CategoryUpdateRequest;
import distove.community.dto.response.ResultResponse;
import distove.community.entity.Category;
import distove.community.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static distove.community.config.AuthorizedRole.Auth.CAN_MANAGE_CHANNEL;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @AuthorizedRole(name = CAN_MANAGE_CHANNEL)
    @PostMapping("/category")
    public ResponseEntity<Object> createNewChannel(@RequestParam Long serverId,
                                                   @RequestBody NewCategoryRequest newCategoryRequest) {
        Category newCategory = categoryService.createNewCategory(serverId, newCategoryRequest.getName());
        return ResultResponse.success(HttpStatus.OK, "카테고리 생성 성공", newCategory);
    }

    @AuthorizedRole(name = CAN_MANAGE_CHANNEL)
    @PatchMapping("/category/{categoryId}")
    public ResponseEntity<Object> updateChannelName(@PathVariable Long categoryId,
                                                    @RequestParam Long serverId,
                                                    @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryService.updateCategoryName(categoryId, categoryUpdateRequest);
        return ResultResponse.success(HttpStatus.OK, "카테고리 이름 수정 성공", category);
    }

}
