package com.example.delivery.payment.controller;

import com.example.delivery.global.exception.ConflictException;
import com.example.delivery.global.exception.ForbiddenException;
import com.example.delivery.global.exception.NotFoundException;
import com.example.delivery.global.exception.RestApiException;
import com.example.delivery.global.security.UserDetailsImpl;
import com.example.delivery.payment.dto.request.PaymentRequest;
import com.example.delivery.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders/{orderId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제
    @PostMapping
    public ResponseEntity<?> pay(@PathVariable Long orderId,
                                 @Valid @RequestBody PaymentRequest requestDto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return new ResponseEntity<>(paymentService.pay(orderId, requestDto, userDetails.getUser()), HttpStatus.CREATED);  // 201
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);     // 404
        } catch (ForbiddenException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);     // 403
        } catch (ConflictException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);       // 409
        }
    }
}