package distove.community.repository;

import distove.community.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoriesByServerId(Long serverId);
    void deleteAllByServerId(Long serverId);
    Optional<Category> findByIdAndServerId(Long categoryId, Long serverId);
}
