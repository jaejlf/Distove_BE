package distove.community.repository;

import distove.community.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findMembersByUserId(Long userId);

    void deleteAllByServerId(Long serverId);

    List<Member> findMembersByServerId(Long serverId);
}
