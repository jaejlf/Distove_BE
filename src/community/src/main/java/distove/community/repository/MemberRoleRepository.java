package distove.community.repository;

import distove.community.entity.MemberRole;
import distove.community.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {
    Optional<MemberRole> findByRoleNameAndServerId(String roleName, Long serverId);
    List<MemberRole> findAllByServerId(Long serverId);
    void deleteMemberRolesByServer(Server server);
}
