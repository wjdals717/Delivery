package com.example.delivery.user.controller;

import com.example.delivery.global.security.JwtUtil;
import com.example.delivery.user.dto.request.LoginRequest;
import com.example.delivery.user.dto.response.LoginResponse;
import com.example.delivery.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 로그인: 토큰을 Authorization 헤더와 응답 본문에 함께 담아 돌려준다
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest requestDto) {
        LoginResponse response = authService.login(requestDto);
        return ResponseEntity.ok()
                .header(JwtUtil.AUTHORIZATION_HEADER, response.getAccessToken())
                .body(response);
    }
}
