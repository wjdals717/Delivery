package com.example.delivery.user.controller;

import com.example.delivery.global.security.JwtUtil;
import com.example.delivery.user.entity.User;
import com.example.delivery.user.entity.UserRoleEnum;
import com.example.delivery.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.save(new User("customer1", passwordEncoder.encode("password123"), "customer1@test.com", UserRoleEnum.CUSTOMER));
    }

    @Test
    @DisplayName("로그인에 성공하면 200과 토큰을 돌려준다")
    void loginSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("customer1", "password123")))
                .andExpect(status().isOk())
                .andExpect(header().string(JwtUtil.AUTHORIZATION_HEADER, org.hamcrest.Matchers.startsWith("Bearer ")))
                .andExpect(jsonPath("$.username").value("customer1"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        String token = result.getResponse().getHeader(JwtUtil.AUTHORIZATION_HEADER).substring(JwtUtil.BEARER_PREFIX.length());
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.getUserInfoFromToken(token).getSubject()).isEqualTo("customer1");
    }

    @Test
    @DisplayName("비밀번호가 틀리면 401")
    void loginWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("customer1", "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401));
    }

    @Test
    @DisplayName("없는 아이디면 401")
    void loginUnknownUsername() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("nobody", "password123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰 없이 보호된 API를 호출하면 거절된다")
    void protectedApiWithoutToken() throws Exception {
        mockMvc.perform(get("/api/protected-sample"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("유효한 토큰이면 보호된 API의 인증을 통과한다")
    void protectedApiWithValidToken() throws Exception {
        String bearer = jwtUtil.createToken("customer1", UserRoleEnum.CUSTOMER);

        // 인증은 통과하고, 없는 경로라 404
        mockMvc.perform(get("/api/protected-sample").header(JwtUtil.AUTHORIZATION_HEADER, bearer))
                .andExpect(status().isNotFound());
    }

    private String loginJson(String username, String password) {
        return """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password);
    }
}
