package com.example.delivery.global.exception;

// 인증 실패 (로그인 실패 등) → 401
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
