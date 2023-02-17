package distove.auth.service;

import distove.auth.exception.DistoveException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
public class JwtTokenProvider {

    private final Key key;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
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
                    .setExpiration(new Date(now.getTime() + Duration.ofMinutes(43200).toMillis()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } else {
            headers.put("type", "RT");
            return Jwts.builder()
                    .setHeader(headers)
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + Duration.ofDays(30).toMillis()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }

    }

    public String getTypeOfToken(String token) {
        Jws<Claims> jws = validToken(token);
        return String.valueOf(jws.getHeader().get("type"));
    }

    public Long getUserId(String token) throws DistoveException {
        Jws<Claims> jws= validToken(token);
        return Long.valueOf(String.valueOf(jws.getBody().get("userId")));
    }

    public Jws<Claims> validToken(String token) throws DistoveException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (SignatureException e) {
            throw new DistoveException(JWT_INVALID);
        } catch (ExpiredJwtException e) {
            throw new DistoveException(JWT_EXPIRED);
        }
    }
}