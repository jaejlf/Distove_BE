package distove.community.repository;

import distove.community.entity.Invitation;
import distove.community.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByCode(String code);

    Optional<Invitation> findByUserIdAndCode(Long userId, String code);

    List<Invitation> findAllByServer(Server server);

    void deleteInvitationsByServer(Server server);
}
