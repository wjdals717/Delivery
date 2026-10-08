package com.example.delivery.order.repository;

import com.example.delivery.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMenuOwnerId(Long id);
    List<Order> findAllByCustomerId(Long id);
}
