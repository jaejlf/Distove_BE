package distove.auth.service;


import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.SignUpRequest;
import distove.auth.dto.response.LogoutResponse;
import distove.auth.dto.response.TokenResponse;
import distove.auth.dto.response.UserResponse;
import distove.auth.entity.User;
import distove.auth.exception.DistoveException;
import distove.auth.repoisitory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static distove.auth.exception.ErrorCode.*;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StorageService storageService;

    public UserResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DistoveException(DUPLICATE_EMAIL);
        }

        User user = new User(request.getEmail(), bCryptPasswordEncoder.encode(request.getPassword()), request.getNickname(), request.getProfileImgUrl());
        userRepository.save(user);

        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new DistoveException(PASSWORD_ERROR);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(request.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
        return TokenResponse.of(accessToken, refreshToken);
    }

    public LogoutResponse logout(String token) {
        User user = userRepository.findByAccessToken(token)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        user.updateRefreshToken(null);
        userRepository.save(user);
        return LogoutResponse.of(user.getEmail());
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

}