package distove.auth.repoisitory;

import distove.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    List<User> findByNickname(String nickname);


    boolean existsByEmail(String email);
}
