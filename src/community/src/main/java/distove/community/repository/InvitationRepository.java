package distove.community.repository;

import distove.community.entity.Invitation;
import distove.community.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByInviteCode(String inviteCode);
    Optional<Invitation> findByUserIdAndInviteCode(Long userId, String inviteCode);
    List<Invitation> findAllByServer(Server server);
    void deleteInvitationsByServer(Server server);
}
