package distove.community.service;

import distove.community.dto.response.CategoryResponse;
import distove.community.entity.Category;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ChannelRepository channelRepository;
    public List<CategoryResponse> getCategoriesByServerId(Long serverId) {
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category : categories) {
            categoryResponses.add(new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    channelRepository.findChannelsByCategoryId(category.getId()))
            );
        };
        return categoryResponses;
    };
}