package distove.community.service;

import distove.community.dto.request.CategoryUpdateRequest;
import distove.community.entity.Category;
import distove.community.entity.Server;
import distove.community.exception.DistoveException;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static distove.community.exception.ErrorCode.CATEGORY_NOT_FOUND;
import static distove.community.exception.ErrorCode.SERVER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ServerRepository serverRepository;

    public Category updateCategoryName(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new DistoveException(CATEGORY_NOT_FOUND));
        category.updateCategory(categoryUpdateRequest.getName());
        categoryRepository.save(category);
        return category;
    }

    public Category createNewCategory(Long serverId, String name) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
        return categoryRepository.save(
                new Category(
                name,
                server
        ));
    }

}
