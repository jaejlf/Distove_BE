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
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static distove.auth.exception.ErrorCode.JWT_EXPIRED;
import static distove.auth.exception.ErrorCode.JWT_INVALID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final Key key;

    @Value("${jwt.access.token.valid.time}")
    private long validTimeAccessToken;

    @Value("${jwt.refresh.token.valid.time}")
    private long validTimeRefreshToken;

    @Autowired
    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }



    public String createToken(Long userId, String type) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        Claims claims = Jwts.claims().setSubject("userId");
        claims.put("userId", userId);

        Date now = new Date();

        if (type.equals("AT")) {
            headers.put("type", "AT");
            return Jwts.builder()
                    .setHeader(headers)
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + Duration.ofMinutes(validTimeAccessToken).toMillis()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } else {
            headers.put("type", "RT");
            return Jwts.builder()
                    .setHeader(headers)
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + Duration.ofDays(validTimeRefreshToken).toMillis()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }
    }

    public void validToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new DistoveException(JWT_EXPIRED);
        } catch (Exception e) {
            throw new DistoveException(JWT_INVALID);
        }
    }

    public String getTypeOfToken(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return String.valueOf(jws.getHeader().get("type"));
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