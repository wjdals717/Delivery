package com.example.delivery.user.service;

import com.example.delivery.global.exception.UnauthorizedException;
import com.example.delivery.global.security.JwtUtil;
import com.example.delivery.user.dto.request.LoginRequest;
import com.example.delivery.user.dto.response.LoginResponse;
import com.example.delivery.user.entity.User;
import com.example.delivery.user.entity.UserRoleEnum;
import com.example.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci1kZWxpdmVyeS1qd3QtdGVzdHMtb25seQ==";

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);
    private final AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil);

    @BeforeEach
    void setUp() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername("owner1")).thenReturn(Optional.of(
                new User("owner1", passwordEncoder.encode("password123"), "owner1@test.com", UserRoleEnum.OWNER)));
    }

    @Test
    @DisplayName("아이디와 비밀번호가 맞으면 토큰을 발급한다")
    void loginIssuesToken() {
        LoginResponse response = authService.login(new LoginRequest("owner1", "password123"));

        assertThat(response.getUsername()).isEqualTo("owner1");
        assertThat(response.getRole()).isEqualTo(UserRoleEnum.OWNER);
        String token = response.getAccessToken().substring(JwtUtil.BEARER_PREFIX.length());
        assertThat(jwtUtil.getUserInfoFromToken(token).get(JwtUtil.AUTHORIZATION_KEY, String.class)).isEqualTo("OWNER");
    }

    @Test
    @DisplayName("비밀번호가 틀리면 UnauthorizedException")
    void wrongPasswordThrows() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("owner1", "wrong-password")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("아이디가 없으면 UnauthorizedException")
    void unknownUsernameThrows() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("nobody", "password123")))
                .isInstanceOf(UnauthorizedException.class);
    }
}
