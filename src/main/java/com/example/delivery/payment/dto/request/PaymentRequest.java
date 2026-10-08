package com.example.delivery.payment.dto.request;

import com.example.delivery.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentRequest {
    @NotNull
    private PaymentMethod method;       // "CARD"만 허용, 그 외 값은 변환 실패로 400
}