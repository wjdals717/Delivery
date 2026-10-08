package com.example.delivery.global.security;

import com.example.delivery.user.entity.User;
import com.example.delivery.user.entity.UserRoleEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci1kZWxpdmVyeS1qd3QtdGVzdHMtb25seQ==";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);

    private final UserDetailsService userDetailsService = username -> {
        if (username.equals("owner1")) {
            return new UserDetailsImpl(new User("owner1", "encoded", "owner1@test.com", UserRoleEnum.OWNER));
        }
        throw new UsernameNotFoundException(username);
    };

    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 토큰이면 인증정보를 넣는다")
    void validTokenSetsAuthentication() throws Exception {
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(requestWith(jwtUtil.createToken("owner1", UserRoleEnum.OWNER)), new MockHttpServletResponse(), chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(((UserDetailsImpl) auth.getPrincipal()).getUsername()).isEqualTo("owner1");
        assertThat(auth.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_OWNER");
        assertThat(chain.getRequest()).isNotNull(); // 다음 필터로 넘어갔다
    }

    @Test
    @DisplayName("토큰이 없으면 인증없이 통과한다")
    void noTokenPassesWithoutAuthentication() throws Exception {
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    @DisplayName("잘못된 토큰이면 인증없이 통과한다")
    void invalidTokenPassesWithoutAuthentication() throws Exception {
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(requestWith("Bearer broken.token.value"), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    @DisplayName("토큰의 사용자가 없으면 인증없이 통과한다")
    void unknownUserPassesWithoutAuthentication() throws Exception {
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(requestWith(jwtUtil.createToken("ghost", UserRoleEnum.CUSTOMER)), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(chain.getRequest()).isNotNull();
    }

    private MockHttpServletRequest requestWith(String bearerToken) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtUtil.AUTHORIZATION_HEADER, bearerToken);
        return request;
    }
}
