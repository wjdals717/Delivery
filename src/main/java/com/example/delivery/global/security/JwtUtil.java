package com.example.delivery.global.security;

import com.example.delivery.user.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간 (60분)
    private static final long TOKEN_TIME = 60 * 60 * 1000L;

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret.key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // 토큰 생성: 아이디(subject), 역할, 만료 시간만 담음
    public String createToken(String username, UserRoleEnum role) {
        Date now = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(username)                                      // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role.name())                  // 사용자 권한
                        .expiration(new Date(now.getTime() + TOKEN_TIME))       // 만료 시간
                        .issuedAt(now)                                          // 발급일
                        .signWith(key)                                          // 암호화 알고리즘
                        .compact();
    }

    // header 에서 JWT 가져오기 (Bearer 접두사 제거)
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // 토큰 검증 (서명, 만료, 형식)
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}