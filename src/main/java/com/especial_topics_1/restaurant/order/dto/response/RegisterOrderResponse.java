package com.especial_topics_1.restaurant.order.dto.response;

import com.especial_topics_1.restaurant.order.Order;
import com.especial_topics_1.restaurant.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RegisterOrderResponse(
        UUID id,
        UUID restaurantId,
        UUID customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt,
        List<OrderItemResponse> items
) {
    public RegisterOrderResponse(Order order) {
        this(
                order.getId(),
                order.getRestaurant().getId(),
                order.getCustomer().getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getOrderItems().stream().map(OrderItemResponse::new).toList()
                );

    }
}
