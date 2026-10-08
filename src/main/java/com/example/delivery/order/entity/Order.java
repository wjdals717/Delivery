package com.example.delivery.order.entity;

import com.example.delivery.global.entity.BaseEntity;
import com.example.delivery.menu.entity.Menu;
import com.example.delivery.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;       // 수량

    @Column(nullable = false)
    private int totalPrice;     // 총액

    @Column(nullable = false)
    private String address;     // 주소

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;      // 주문한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;          // 주문 메뉴

    public Order(int quantity, int totalPrice, String address, User customer, Menu menu) {
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.address = address;
        this.customer = customer;
        this.menu = menu;
        this.status = OrderStatus.ORDERED;     // 처음 상태: 주문요청
    }
}