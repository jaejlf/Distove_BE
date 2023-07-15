package distove.community.service;

import distove.community.exception.DistoveException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Objects;

import static distove.community.exception.ErrorCode.*;

@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public void validateToken(String token, String type) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            String headerType = String.valueOf(jws.getHeader().get("type"));
            if (!Objects.equals(headerType, type)) throw new DistoveException(JWT_INVALID_ERROR);
        } catch (ExpiredJwtException e) {
            throw new DistoveException(JWT_EXPIRED_ERROR);
        } catch (Exception e) {
            throw new DistoveException(JWT_INVALID_ERROR);
        }
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
