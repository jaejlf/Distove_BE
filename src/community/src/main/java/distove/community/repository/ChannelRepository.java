package distove.community.repository;

import distove.community.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel.Info> findChannelsByCategoryId(Long id);
    List<Channel> deleteAllByCategoryId(Long categoryId);
}
