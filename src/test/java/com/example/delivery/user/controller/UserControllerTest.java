package com.example.delivery.user.controller;

import com.example.delivery.global.config.SecurityConfig;
import com.example.delivery.user.dto.request.SignupRequest;
import com.example.delivery.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공 시 201")
    void signupSuccess() throws Exception {
        String body = """
                {"username":"tester","password":"password123","email":"tester@example.com","owner":true,"adminToken":"token"}
                """;

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        verify(userService).signup(argThat((SignupRequest req) ->
                req.getUsername().equals("tester")
                        && req.getEmail().equals("tester@example.com")
                        && req.isOwner()
                        && "token".equals(req.getAdminToken())));
    }

    @Test
    @DisplayName("입력값 검증 실패 시 400")
    void signupValidationFail() throws Exception {
        String body = """
                {"username":"ab","password":"short","email":"not-an-email"}
                """;

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(userService, never()).signup(any());
    }

    @Test
    @DisplayName("중복 사용자면 400")
    void signupDuplicateUser() throws Exception {
        willThrow(new IllegalArgumentException("중복된 사용자가 존재합니다."))
                .given(userService).signup(any());

        String body = """
                {"username":"tester","password":"password123","email":"tester@example.com"}
                """;

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("중복된 사용자가 존재합니다."));
    }

    @Test
    @DisplayName("인증 없는 다른 요청은 차단")
    void otherRequestsRequireAuth() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }
}
