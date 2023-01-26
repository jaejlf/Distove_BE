package distove.auth.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/new")
    public User createUser(@RequestBody Map<String, String> request) {
        return userRepository.save(new User(request.get("nickname"), request.get("profileImgUrl")));
    }

    @GetMapping("/user")
    public User getUser(@RequestHeader("userId") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.get();
    }

}
