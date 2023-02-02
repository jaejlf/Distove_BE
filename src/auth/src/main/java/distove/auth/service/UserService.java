package distove.auth.service;


import distove.auth.dto.request.*;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static distove.auth.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StorageService storageService;

    @Value("${default.image.address}")
    private String defaultImageUrl;

    public UserResponse join(JoinRequest request) {
        String profileImgUrl;

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DistoveException(DUPLICATE_EMAIL);
        }

        if (request.getProfileImg().isEmpty()) {
            profileImgUrl = defaultImageUrl;
        } else {
            profileImgUrl = storageService.uploadToS3(request.getProfileImg());
        }


        User user = new User(request.getEmail(), bCryptPasswordEncoder.encode(request.getPassword()), request.getNickname(), profileImgUrl);
        userRepository.save(user);

        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new DistoveException(PASSWORD_ERROR);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
        return TokenResponse.of(accessToken, refreshToken);
    }

    public LogoutResponse logout(String token) {
        log.info("dd : {}", jwtTokenProvider.getUserId(token).toString());
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
            users.add(UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl()));
        }
        return users;
    }

    public UserResponse updateNickname(String token, UpdateNicknameRequest request) {
        User user = userRepository.findById(jwtTokenProvider.getUserId(token))
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        user.updateUserNickname(request.getNickname());
        userRepository.save(user);
        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }


    public UserResponse updateProfileImage(String token, UpdateProfileImageRequest request) {
        User user = userRepository.findById(jwtTokenProvider.getUserId(token))
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        String profileImgUrl = storageService.updateS3(request.getProfileImg(), user.getProfileImgUrl().split("/")[3]);

        user.updateUserProfileImageUrl(profileImgUrl);
        userRepository.save(user);
        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    public TokenResponse reissue(String token) {
        log.info("1 = {}", token);
        log.info("2 = {}", getUserIdFromDatabase(token).toString());
        log.info("3 = {}", jwtTokenProvider.generateAccessToken(getUserIdFromDatabase(token)), token);
        return TokenResponse.of(jwtTokenProvider.generateAccessToken(getUserIdFromDatabase(token)), token);
    }


    public Long getUserIdFromDatabase(String token) {
        log.info("유저아이디 get = {}", jwtTokenProvider.getUserId(token).toString());
        User user = userRepository.findById(jwtTokenProvider.getUserId(token))
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));
        log.info(user.getEmail());
        log.info("유저 아이디", user.getId());
        return user.getId();


    }

    public boolean checkEmailDuplicate (EmailDuplicateRequest request){
        return userRepository.existsByEmail(request.getEmail());
    }
}