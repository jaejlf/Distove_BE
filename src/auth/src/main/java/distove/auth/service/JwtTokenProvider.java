package distove.auth.service;

import distove.auth.dto.response.TokenResponse;
import distove.auth.exception.DistoveException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static distove.auth.exception.ErrorCode.JWT_EXPIRED;
import static distove.auth.exception.ErrorCode.JWT_INVALID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "AT");
        headers.put("alg", "HS256");
        Claims claims = Jwts.claims().setSubject("userId");
        claims.put("userId", userId);

        Date now = new Date();

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(0).toMillis()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateRefreshToken(Long userId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "RT");
        headers.put("alg", "HS256");
        Claims claims = Jwts.claims().setSubject("userId");
        claims.put("userId", userId);

        Date now = new Date();

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofDays(30).toMillis()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 검증
     * - 헤더의 토큰을 받아 토큰을 sign한 key와 expire되었는지 검증하는 메서드
     * 피드백 받고 싶은 부분
     * - 토큰 검증 과정에서 try-catch 구문을 많이 사용하게 되는데 예외 처리하는 최선의 방법일지 궁금합니다.
     */
    public boolean validToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (UnsupportedJwtException e) {
            throw new DistoveException(JWT_INVALID);
        } catch (ExpiredJwtException e) {
            throw new DistoveException(JWT_EXPIRED);
        }
    }

    public TokenResponse reissue(String token) {
        return TokenResponse.of(generateAccessToken(getUserId(token)), token);
    }

    public Long getUserId(String token) throws DistoveException {
        try {
            return Long.valueOf(String.valueOf(Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId")));
        } catch (UnsupportedJwtException e) {
            throw new DistoveException(JWT_INVALID);
        } catch (ExpiredJwtException e) {
            throw new DistoveException(JWT_EXPIRED);
        }
    }
}