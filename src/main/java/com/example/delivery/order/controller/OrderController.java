package com.example.delivery.order.controller;

import com.example.delivery.global.exception.ConflictException;
import com.example.delivery.global.exception.ForbiddenException;
import com.example.delivery.global.exception.NotFoundException;
import com.example.delivery.global.exception.RestApiException;
import com.example.delivery.global.security.UserDetailsImpl;
import com.example.delivery.order.dto.request.OrderRequest;
import com.example.delivery.order.dto.response.OrderResponse;
import com.example.delivery.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest requestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return new ResponseEntity<>(orderService.createOrder(requestDto, userDetails.getUser()), HttpStatus.CREATED);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND); //404
        }
    }

    // 주문 목록 조회(복수)
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return new ResponseEntity<>(orderService.getOrders(userDetails.getUser()), HttpStatus.OK);
    }

    // 주문 취소
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return new ResponseEntity<>(orderService.cancelOrder(orderId, userDetails.getUser()), HttpStatus.OK);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);     //404
        } catch (ForbiddenException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);     //403
        } catch (ConflictException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);       //409
        }
    }

    // 주문 수락 (결제완료 → 주문수락)
    @PatchMapping("/{orderId}/accept")
    public ResponseEntity<?> acceptOrder(@PathVariable Long orderId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return new ResponseEntity<>(orderService.acceptOrder(orderId, userDetails.getUser()), HttpStatus.OK);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (ForbiddenException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        } catch (ConflictException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        }
    }

    // 배달 완료 (주문수락 → 배달완료)
    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long orderId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return new ResponseEntity<>(orderService.completeOrder(orderId, userDetails.getUser()), HttpStatus.OK);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        } catch (ForbiddenException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        } catch (ConflictException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        }
    }
}