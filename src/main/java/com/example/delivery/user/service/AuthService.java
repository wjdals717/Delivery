package com.example.delivery.user.service;

import com.example.delivery.global.exception.UnauthorizedException;
import com.example.delivery.global.security.JwtUtil;
import com.example.delivery.user.dto.request.LoginRequest;
import com.example.delivery.user.dto.response.LoginResponse;
import com.example.delivery.user.entity.User;
import com.example.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest requestDto) {
        // 아이디가 없거나 비밀번호가 틀린 경우 같은 메시지로 401 (어느 쪽이 틀렸는지 알려주지 않는다)
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        return new LoginResponse(user.getUsername(), user.getRole(), token);
    }
}
