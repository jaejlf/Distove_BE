package distove.auth.service;


import distove.auth.dto.reponse.TokenResponse;
import distove.auth.dto.request.SignUpRequest;
import distove.auth.entity.User;
import distove.auth.reoisitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signUp(SignUpRequest request) {
        // 예외 처리
        User user = new User(request.getEmail(), "", false, LocalDateTime.now(), LocalDateTime.now(), bCryptPasswordEncoder.encode(request.getPassword()), "refreshToken", request.getNickname());
        userRepository.save(user);
    }

    public TokenResponse login(User loginuser) {
        User user = userRepository.findByEmail(loginuser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 Email 입니다."));

        if (!bCryptPasswordEncoder.matches(loginuser.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(loginuser.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(loginuser.getEmail());
        return TokenResponse.of(accessToken, refreshToken);
    }
}
