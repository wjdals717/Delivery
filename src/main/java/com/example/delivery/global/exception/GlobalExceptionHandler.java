package com.example.delivery.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<RestApiException> illegalArgumentExceptionHandler(IllegalArgumentException ex) {

        RestApiException restApiException =
                new RestApiException(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                );

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    // @Valid 검증 실패
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<RestApiException> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        RestApiException restApiException =
                new RestApiException(
                        message,
                        HttpStatus.BAD_REQUEST.value()
                );

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    // 로그인 실패 (아이디 없음 / 비밀번호 불일치)
    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<RestApiException> unauthorizedExceptionHandler(UnauthorizedException ex) {

        RestApiException restApiException =
                new RestApiException(
                        ex.getMessage(),
                        HttpStatus.UNAUTHORIZED.value()
                );

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.UNAUTHORIZED
        );
    }

}
