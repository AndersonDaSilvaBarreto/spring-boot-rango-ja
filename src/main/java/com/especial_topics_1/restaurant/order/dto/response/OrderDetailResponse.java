package com.especial_topics_1.restaurant.order.dto.response;

import com.especial_topics_1.restaurant.coupon.DiscountType;
import com.especial_topics_1.restaurant.order.Order;
import com.especial_topics_1.restaurant.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
        UUID id,
        String restaurantName,
        String restaurantLogoUrl,
        String customerName,
        String customerPhone,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        String couponName,
        DiscountType discountType,
        OrderStatus status,
        Instant createdAt,
        List<OrderItemResponse> items
) {
    public OrderDetailResponse(Order order) {
        this(
                order.getId(),
                order.getRestaurant().getName(),
                order.getRestaurant().getLogoUrl(),
                order.getCustomer().getName(),
                order.getCustomer().getPhone(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getCoupon() != null ? order.getCoupon().getName() : null
                ,
                order.getCoupon() != null ? order.getCoupon().getDiscountType() : null,
                order.getStatus(),
                order.getCreatedAt(),
                order.getOrderItems().stream().map(OrderItemResponse::new).toList()
        );
    }
}
