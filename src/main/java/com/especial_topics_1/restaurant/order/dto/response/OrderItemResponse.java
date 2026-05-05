package com.especial_topics_1.restaurant.order.dto.response;

import com.especial_topics_1.restaurant.order.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID dishId,
        String dishName,
        String imageUrl,
        Integer quantity,
        BigDecimal unitPrice
) {
    public OrderItemResponse(OrderItem item) {
        this(
                item.getDish().getId(),
                item.getDish().getName(),
                item.getDish().getImageUrl(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}
