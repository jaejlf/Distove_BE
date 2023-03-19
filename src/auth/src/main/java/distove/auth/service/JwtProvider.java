package distove.auth.service;

import distove.auth.exception.DistoveException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static distove.auth.exception.ErrorCode.JWT_EXPIRED_ERROR;
import static distove.auth.exception.ErrorCode.JWT_INVALID_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final Key key;

    @Value("${jwt.access.token.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.refresh.token.valid.time}")
    private long refreshTokenValidTime;

    @Autowired
    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, "AT", accessTokenValidTime);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, "RT", refreshTokenValidTime);
    }

    public String createToken(Long userId, String type, Long tokenValidTime) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        Claims claims = Jwts.claims().setSubject("userId");
        claims.put("userId", userId);

        Date now = new Date();
        headers.put("type", type);
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new DistoveException(JWT_EXPIRED_ERROR);
        } catch (Exception e) {
            throw new DistoveException(JWT_INVALID_ERROR);
        }
    }

    public void validateRefreshToken(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        String type = String.valueOf(jws.getHeader().get("type"));
        if (!Objects.equals((type), "RT")) throw new DistoveException(JWT_INVALID_ERROR);
    }

    public Long getUserId(String token) throws DistoveException {
        return Long.valueOf(String.valueOf(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("userId")));
    }

}