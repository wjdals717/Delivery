package com.example.delivery.payment.service;

import com.example.delivery.global.exception.ConflictException;
import com.example.delivery.global.exception.ForbiddenException;
import com.example.delivery.global.exception.NotFoundException;
import com.example.delivery.order.entity.Order;
import com.example.delivery.order.entity.OrderStatus;
import com.example.delivery.order.repository.OrderRepository;
import com.example.delivery.payment.dto.request.PaymentRequest;
import com.example.delivery.payment.dto.response.PaymentResponse;
import com.example.delivery.payment.entity.Payment;
import com.example.delivery.payment.repository.PaymentRepository;
import com.example.delivery.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse pay(Long orderId, PaymentRequest requestDto, User user) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("주문이 존재하지 않습니다."));               // 404

        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new ForbiddenException("본인 주문만 결제할 수 있습니다.");              // 403
        }
        if (order.getStatus() != OrderStatus.ORDERED) {     // 이미 결제됐거나(PAID 이후) 취소된 주문은 거절
            throw new ConflictException("주문요청 상태에서만 결제할 수 있습니다.");        // 409
        }

        Payment payment = paymentRepository.save(
                new Payment(order.getTotalPrice(), requestDto.getMethod(), order));     // 금액 = 주문 총액
        order.setStatus(OrderStatus.PAID);                  // 주문요청 → 결제완료 (더티 체킹으로 저장)
        return new PaymentResponse(payment);
    }
}