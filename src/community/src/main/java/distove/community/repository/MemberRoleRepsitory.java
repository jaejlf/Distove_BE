package distove.community.repository;

import distove.community.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRoleRepsitory extends JpaRepository<MemberRole, Long> {
    Optional<MemberRole> findByRoleNameAndServerId(String roleName, Long serverId);
}
