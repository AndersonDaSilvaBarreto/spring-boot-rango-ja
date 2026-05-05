package com.especial_topics_1.restaurant.order.dto.response;

import com.especial_topics_1.restaurant.order.Order;
import com.especial_topics_1.restaurant.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RestaurantOrderSummaryResponse(
        UUID id,
        String customerName,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt
) {
    public RestaurantOrderSummaryResponse(Order order) {
        this(
                order.getId(),
                order.getCustomer().getName(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
