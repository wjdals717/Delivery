package com.example.delivery.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler({DuplicateException.class})
    public ResponseEntity<RestApiException> duplicateExceptionHandler(DuplicateException ex) {

        RestApiException restApiException =
                new RestApiException(
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value()
                );

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.CONFLICT
        );
    }

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