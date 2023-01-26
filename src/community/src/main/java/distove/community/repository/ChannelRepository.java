package distove.community.repository;

import distove.community.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository

public interface ChannelRepository extends JpaRepository<Channel,Long> {

    Channel save(Channel channel);
    Optional<Channel> findById(Long id);
    void deleteById(Long id);
    List<Channel.Info> findChannelsByCategoryId(Long id);

}
