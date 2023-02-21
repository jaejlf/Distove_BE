package distove.community.repository;

import distove.community.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findMembersByUserId(Long userId);
    Optional<Member> findByUserIdAndServerId(Long userId, Long serverId);
    void deleteAllByServerId(Long serverId);
    boolean existsByUserIdAndServerId(Long userId, Long serverId);

    @Lock(LockModeType.OPTIMISTIC)
    List<Member> findMembersByServerId(Long serverId);
}
