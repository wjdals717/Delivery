package com.example.delivery.payment.dto.response;

import com.example.delivery.order.entity.OrderStatus;
import com.example.delivery.payment.entity.Payment;
import com.example.delivery.payment.entity.PaymentMethod;
import com.example.delivery.payment.entity.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

// response/PaymentResponse.java
@Getter
public class PaymentResponse {
    private final Long paymentId;
    private final Long orderId;
    private final int amount;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final OrderStatus orderStatus;   // 결제 후 주문 상태 (PAID)
    private final LocalDateTime paidAt;

    public PaymentResponse(Payment payment) {
        this.paymentId = payment.getId();
        this.orderId = payment.getOrder().getId();
        this.amount = payment.getAmount();
        this.method = payment.getMethod();
        this.status = payment.getStatus();
        this.orderStatus = payment.getOrder().getStatus();
        this.paidAt = payment.getCreatedAt();
    }
}