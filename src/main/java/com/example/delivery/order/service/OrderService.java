package com.example.delivery.order.service;

import com.example.delivery.global.exception.ConflictException;
import com.example.delivery.global.exception.ForbiddenException;
import com.example.delivery.global.exception.NotFoundException;
import com.example.delivery.menu.entity.Menu;
import com.example.delivery.menu.repository.MenuRepository;
import com.example.delivery.order.dto.request.OrderRequest;
import com.example.delivery.order.dto.response.OrderResponse;
import com.example.delivery.order.entity.Order;
import com.example.delivery.order.entity.OrderStatus;
import com.example.delivery.order.repository.OrderRepository;
import com.example.delivery.user.entity.User;
import com.example.delivery.user.entity.UserRoleEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public Object createOrder(@Valid OrderRequest requestDto, User user) {
        // 메뉴가 활성화 되어 있는지 조회(조건 : ID, Deleted= false)
        Menu menu = menuRepository.findByIdAndDeletedFalse(requestDto.getMenuId()).orElseThrow(
                () -> new NotFoundException("메뉴가 존재하지 않습니다."));         // 404

        int totalPrice = menu.getPrice() * requestDto.getQuantity();                  // 총 가격 계산 = 메뉴 가격 * 주문 수량
        Order order = orderRepository.save(
                new Order(requestDto.getQuantity(), totalPrice, requestDto.getAddress(), user, menu));
        return new OrderResponse(order);
    }

    public List<OrderResponse> getOrders(User user) {
        // CUSTOMER : 본인이 한 주문 조회, OWNER : 본인 메뉴에 들어온 주문 조회
        List<Order> orderList = (user.getRole() == UserRoleEnum.OWNER)      // 권한 확인
                ? orderRepository.findAllByMenuOwnerId(user.getId())        // 손님: 내가 한 주문
                : orderRepository.findAllByCustomerId(user.getId());        // 사장님: 내 메뉴에 들어온 주문
        return orderList.stream().map(OrderResponse::new).toList();
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId, User user) {
        Order order = findOrder(orderId);                                                  // 404
        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new ForbiddenException("본인 주문만 취소할 수 있습니다.");                    // 403
        }
        if (order.getStatus() != OrderStatus.ORDERED) {     // 주문요청 상태일 때만 취소 가능, ORDERED가 아니면 예외 처리
            throw new ConflictException("주문요청 상태에서만 취소할 수 있습니다.");              // 409
        }
        order.setStatus(OrderStatus.CANCELED);
        return new OrderResponse(order);
    }

    public OrderResponse acceptOrder(Long orderId, User user) {
        return changeStatus(orderId, OrderStatus.PAID, OrderStatus.ACCEPTED, user);
    }

    public Object completeOrder(Long orderId, User user) {
        return changeStatus(orderId, OrderStatus.ACCEPTED, OrderStatus.COMPLETED, user);
    }

    private OrderResponse changeStatus(Long orderId, OrderStatus from, OrderStatus to, User user) {
        Order order = findOrder(orderId);                                                  // 404
        if (!order.getMenu().getOwner().getId().equals(user.getId())) {     // 사장님의 메뉴인지 확인
            throw new ForbiddenException("본인 메뉴에 들어온 주문만 변경할 수 있습니다.");        // 403
        }
        if (order.getStatus() != from) {  // 변경 가능한 시점 확인, PAID -> ACCEPTED or ACCEPTED -> COMPLETED 경우만 가능
            throw new ConflictException("변경할 수 없는 상태입니다.");                          // 409
        }
        order.setStatus(to);
        return new OrderResponse(order);
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("주문이 존재하지 않습니다."));
    }
}
