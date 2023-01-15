package distove.community.repository;

import distove.community.entity.Member;
import distove.community.entity.Server;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.util.List;

@Repository
public interface MemberRepository  extends JpaRepository<Member,Long> {
//    @EntityGraph(value="Member.server")
    List<Member> findMembersByUserId(Long userId);
}
