package distove.auth.service;


import distove.auth.dto.request.JoinRequest;
import distove.auth.dto.request.LoginRequest;
import distove.auth.dto.response.LoginResponse;
import distove.auth.dto.response.UserResponse;
import distove.auth.entity.User;
import distove.auth.exception.DistoveException;
import distove.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static distove.auth.exception.ErrorCode.*;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;
    private final StorageService storageService;

    @Value("${max.age.seconds}")
    private static long maxAgeSeconds;

    public UserResponse join(JoinRequest request) {
        validateEmail(request.getEmail());

        String profileImgUrl = storageService.uploadToS3(request.getFile());
        User user = userRepository.save(new User(
                request.getEmail(),
                bCryptPasswordEncoder.encode(request.getPassword()),
                request.getNickname(),
                profileImgUrl));
        return UserResponse.of(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND_ERROR));
        validatePassword(request.getPassword(), user.getPassword());

        String accessToken = jwtProvider.createAccessToken(user.getId());
        return LoginResponse.of(accessToken, user);
    }

    public String createCookie(Long userId) {
        User user = userRepository.findById(userId).get();

        String refreshToken = jwtProvider.createRefreshToken(userId);
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(maxAgeSeconds)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build().toString();
    }

    public UserResponse logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND_ERROR));

        user.updateRefreshToken(null);
        userRepository.save(user);
        return UserResponse.of(user);
    }

    public LoginResponse reissue(HttpServletRequest request) {
        String token = getRefreshToken(request);
        jwtProvider.validateToken(token, "RT");

        User user = userRepository.findByRefreshToken(token)
                .orElseThrow(() -> new DistoveException(JWT_INVALID_ERROR));

        String accessToken = jwtProvider.createAccessToken(user.getId());
        return LoginResponse.of(accessToken, user);
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DistoveException(ACCOUNT_NOT_FOUND_ERROR));
        return UserResponse.of(user);
    }

    public List<UserResponse> getUsersById(List<Long> userIds) {
        List<User> users = userRepository.findByIdIn(userIds);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(UserResponse.of(user));
        }
        return userResponses;
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) throw new DistoveException(ALREADY_EXIST_EMAIL_ERROR);
    }

    private void validatePassword(String input, String real) {
        if (!bCryptPasswordEncoder.matches(input, real)) {
            throw new DistoveException(INVALID_PASSWORD_ERROR);
        }
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
        throw new DistoveException(JWT_INVALID_ERROR);
    }

}
