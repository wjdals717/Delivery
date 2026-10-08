package com.example.delivery.global.security;

import com.example.delivery.user.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci1kZWxpdmVyeS1qd3QtdGVzdHMtb25seQ==";
    private static final String OTHER_SECRET = "b3RoZXItc2VjcmV0LWtleS1mb3ItZGVsaXZlcnktand0LXRlc3RzLW9ubHk=";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);

    @Test
    @DisplayName("토큰에 아이디와 역할과 만료시간이 담긴다")
    void createTokenContainsUsernameRoleAndExpiration() {
        String bearer = jwtUtil.createToken("customer1", UserRoleEnum.CUSTOMER);

        assertThat(bearer).startsWith(JwtUtil.BEARER_PREFIX);
        String token = bearer.substring(JwtUtil.BEARER_PREFIX.length());

        assertThat(jwtUtil.validateToken(token)).isTrue();
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        assertThat(claims.getSubject()).isEqualTo("customer1");
        assertThat(claims.get(JwtUtil.AUTHORIZATION_KEY, String.class)).isEqualTo("CUSTOMER");
        assertThat(claims.getExpiration()).isAfter(new Date());
        assertThat(claims).doesNotContainKey("password");
    }

    @Test
    @DisplayName("만료된 토큰은 유효하지 않다")
    void expiredTokenIsInvalid() {
        JwtUtil expiredJwtUtil = new JwtUtil(SECRET, -1_000);
        String token = strip(expiredJwtUtil.createToken("customer1", UserRoleEnum.CUSTOMER));

        assertThat(jwtUtil.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("다른 키로 서명한 토큰은 유효하지 않다")
    void tokenSignedWithOtherKeyIsInvalid() {
        String forged = Jwts.builder()
                .subject("owner1")
                .claim(JwtUtil.AUTHORIZATION_KEY, "OWNER")
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(OTHER_SECRET)))
                .compact();

        assertThat(jwtUtil.validateToken(forged)).isFalse();
    }

    @Test
    @DisplayName("형식이 깨진 토큰은 유효하지 않다")
    void malformedTokenIsInvalid() {
        assertThat(jwtUtil.validateToken("not-a-jwt")).isFalse();
        assertThat(jwtUtil.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("헤더에서 Bearer 뒤의 토큰만 꺼낸다")
    void getJwtFromHeaderStripsBearerPrefix() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtUtil.AUTHORIZATION_HEADER, "Bearer abc.def.ghi");
        assertThat(jwtUtil.getJwtFromHeader(request)).isEqualTo("abc.def.ghi");

        MockHttpServletRequest noBearer = new MockHttpServletRequest();
        noBearer.addHeader(JwtUtil.AUTHORIZATION_HEADER, "abc.def.ghi");
        assertThat(jwtUtil.getJwtFromHeader(noBearer)).isNull();

        assertThat(jwtUtil.getJwtFromHeader(new MockHttpServletRequest())).isNull();
    }

    private String strip(String bearer) {
        return bearer.substring(JwtUtil.BEARER_PREFIX.length());
    }
}
