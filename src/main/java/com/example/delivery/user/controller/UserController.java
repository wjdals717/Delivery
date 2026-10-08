package com.example.delivery.user.controller;

import com.example.delivery.user.dto.request.SignupRequest;
import com.example.delivery.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest requestDto) {
        // Validation 예외는 GlobalExceptionHandler 에서 400 으로 처리
        userService.signup(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
