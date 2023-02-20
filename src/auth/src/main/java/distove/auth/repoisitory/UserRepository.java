package distove.auth.repoisitory;

import distove.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<Long> findByNickname(String nickname);

    boolean existsByEmail(String email);
}
