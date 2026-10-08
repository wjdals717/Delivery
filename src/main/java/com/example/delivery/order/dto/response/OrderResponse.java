package com.example.delivery.order.dto.response;

import com.example.delivery.order.entity.Order;
import com.example.delivery.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private final Long orderId;
    private final Long menuId;
    private final String menuName;
    private final int quantity;
    private final int totalPrice;
    private final String address;
    private final OrderStatus status;
    private final LocalDateTime orderedAt;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.menuId = order.getMenu().getId();
        this.menuName = order.getMenu().getName();
        this.quantity = order.getQuantity();
        this.totalPrice = order.getTotalPrice();
        this.address = order.getAddress();
        this.status = order.getStatus();
        this.orderedAt = order.getCreatedAt();
    }
}