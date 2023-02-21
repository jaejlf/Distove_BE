package distove.auth.service;


import distove.auth.dto.request.JoinRequest;
import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.request.UpdateNicknameRequest;
import distove.auth.dto.request.UpdateProfileImgRequest;
import distove.auth.dto.response.LoginResponse;
import distove.auth.dto.response.UserResponse;
import distove.auth.entity.User;
import distove.auth.exception.DistoveException;
import distove.auth.repoisitory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static distove.auth.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;
    private final StorageService storageService;

    @Value("${default.img.address}")
    private String defaultImgUrl;

    public UserResponse join(JoinRequest request) {
        String profileImgUrl = null;

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DistoveException(DUPLICATE_EMAIL);
        }

        if (request.getProfileImg() != null && !(request.getProfileImg().isEmpty())) {
            profileImgUrl = storageService.uploadToS3(request.getProfileImg());
        }

        User user = new User(request.getEmail(), bCryptPasswordEncoder.encode(request.getPassword()), request.getNickname(), profileImgUrl);
        userRepository.save(user);

        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new DistoveException(INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createToken(user.getId(), "AT");
        UserResponse loginInfo = UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());

        return LoginResponse.of(accessToken, loginInfo);
    }

    public String setCookieOnLogin(Long userId) {
        String refreshToken = jwtProvider.createToken(userId, "RT");
        User user = userRepository.findById(userId).orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
        return createCookie(refreshToken);
    }

    public UserResponse logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        user.updateRefreshToken(null);
        userRepository.save(user);
        return UserResponse.of(user.getId(), user.getNickname(), user.getEmail());
    }

    public UserResponse updateNickname(Long userId, UpdateNicknameRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        user.updateNickname(request.getNickname());
        userRepository.save(user);
        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    public UserResponse updateProfileImg(Long userId, UpdateProfileImgRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        String profileImgUrl = getProfileImgUrl(request);
        deletePreviousProfileImg(user);
        updateProfileImgUrl(user, profileImgUrl);
        userRepository.save(user);

        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    private String getProfileImgUrl(UpdateProfileImgRequest request) {
        if (request.getProfileImg() != null && !request.getProfileImg().isEmpty()) {
            return storageService.uploadToS3(request.getProfileImg());
        } else {
            return null;
        }
    }

    private void deletePreviousProfileImg(User user) {
        if (user.getProfileImgUrl() != null) {
            storageService.deleteFile(user.getProfileImgUrl());
        }
    }

    private void updateProfileImgUrl(User user, String profileImgUrl) {
        user.updateProfileImgUrl(profileImgUrl);
    }

    public LoginResponse reissue(HttpServletRequest request) {
        String token = getRefreshToken(request);
        if (jwtProvider.getTypeOfToken(token).equals("RT")) {
            User user = userRepository.findByRefreshToken(token)
                    .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

            return LoginResponse.of(jwtProvider.createToken(user.getId(),"AT"), UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl()));
        }
        throw new DistoveException(JWT_INVALID);
    }

    public String setCookieOnReissue(HttpServletRequest request) {
        String refreshToken = getRefreshToken(request);
        return createCookie(refreshToken);
    }

    private static String createCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(60 * 60 * 24 * 30)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("distove.onstove.com")
                .build().toString();
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        throw new DistoveException(JWT_INVALID);
    }

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));
        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }

    public List<UserResponse> getUsers(List<Long> userIds) {
        List<UserResponse> users = new ArrayList<>();

        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));
            users.add(UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl()));
        }
        return users;
    }

    public UserResponse getUserIdsByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND));

        return UserResponse.of(user.getId(), user.getNickname(), user.getProfileImgUrl());
    }
}
