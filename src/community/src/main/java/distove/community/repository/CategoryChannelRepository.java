package distove.community.repository;

import distove.community.entity.CategoryChannel;
import distove.community.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryChannelRepository extends JpaRepository<CategoryChannel,Long> {
    CategoryChannel save(CategoryChannel categoryChannel);
    List<CategoryChannel> findCategoryChannelByCategory_Server_Id(Long ServerId);
}
