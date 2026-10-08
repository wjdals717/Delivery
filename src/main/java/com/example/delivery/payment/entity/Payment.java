package com.example.delivery.payment.entity;

import com.example.delivery.global.entity.BaseEntity;
import com.example.delivery.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int amount;                 // 결제 금액 (= 주문 총액)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;       // 결제 수단

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;       // 결제 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;                // 어떤 주문의 결제인지

    public Payment(int amount, PaymentMethod method, Order order) {
        this.amount = amount;
        this.method = method;
        this.order = order;
        this.status = PaymentStatus.PAID;   // 결제 성공 시 생성되므로 처음부터 결제완료
    }
}