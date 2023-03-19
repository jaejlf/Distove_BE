package distove.community.repository;

import distove.community.entity.Category;
import distove.community.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findChannelsByCategoryIn(List<Category> categories);

    List<Channel> findChannelsByCategoryInAndChannelTypeIdEquals(List<Category> categories, Integer channelTypeId);

    void deleteAllByCategoryIn(List<Category> categories);
}
