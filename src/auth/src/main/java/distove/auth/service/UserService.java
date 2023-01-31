package distove.auth.service;


import distove.auth.dto.request.EmailDuplicateRequest;
import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.UpdateRequest;
import distove.auth.dto.request.SignUpRequest;
import distove.auth.dto.response.LogoutResponse;
import distove.auth.dto.response.TokenResponse;
import distove.auth.dto.response.UserResponse;
import distove.auth.entity.User;
import distove.auth.exception.DistoveException;
import distove.auth.repoisitory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Value("${default.image.address}")
    private String defaultImageUrl;


    public UserResponse signUp(SignUpRequest request) {
        String profileImgUrl;

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DistoveException(DUPLICATE_EMAIL);
        }

        if (request.getProfileImg().isEmpty()) {
            profileImgUrl = defaultImageUrl;
        }
        else {
            profileImgUrl = storageService.uploadToS3(request.getProfileImg());
        }


        User user = new User(request.getEmail(), bCryptPasswordEncoder.encode(request.getPassword()), request.getNickname(), profileImgUrl);
        userRepository.save(user);

        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    /**
     로그인
     - signUp한 유저가 이메일과 패스워드를 사용해 로그인을 하게 되면 이메일이 DB에 있는지 확인 후 액세스 토큰과 리프레시 토큰 발급
     피드백 받고 싶은 부분
     - 로그인을 하는 것은 인증을 한다는 것과 같다고 생각해서 로그인을 하면 AccessToken과 RefreshToken을 재생성 해주는데 RefreshToken을 계속 재생성 해줘도 괜찮을지 궁금합니다.
     */
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new DistoveException(PASSWORD_ERROR);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(request.getEmail(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
        return TokenResponse.of(accessToken, refreshToken);
    }

    /**
     로그아웃
     - 로그아웃 하고자 하는 토큰을 받아 유저Pk를 추출해 DB에서 refreshToken을 null 값으로 변경
     피드백 받고 싶은 부분
     - 리프레시 토큰을 Redis 사용하지 않고 RDB에서 관리해보려 하는데 null 처리를 해서 토큰 관리를 해도 괜찮을지?
     */
    public LogoutResponse logout(String token) {
        User user = userRepository.findById(jwtTokenProvider.getUserId(token))
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        user.updateRefreshToken(null);
        userRepository.save(user);
        return LogoutResponse.of(user.getEmail());
    }

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));
        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    public List<UserResponse> getUsers(List<Long> usersId) {
        List<UserResponse> users = new ArrayList<>();

        for (Long userId : usersId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));
            users.add(UserResponse.of(user.getId(),user.getNickname(),user.getProfileImgUrl()));
        }
        return users;
    }

    public UserResponse updateUser(UpdateRequest request) {
        User user = userRepository.findById(jwtTokenProvider.getUserId(request.getToken()))
                .orElseThrow(() ->new DistoveException(ACCOUNT_NOT_FOUND));

        user.updateUser(request.getNickname());

        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    public boolean checkEmailDuplicate(EmailDuplicateRequest request) {
        return userRepository.existsByEmail(request.getEmail());
    }

}