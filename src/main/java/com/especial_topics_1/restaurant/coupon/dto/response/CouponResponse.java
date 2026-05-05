package com.especial_topics_1.restaurant.coupon.dto.response;

import com.especial_topics_1.restaurant.coupon.Coupon;
import com.especial_topics_1.restaurant.coupon.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String name,
        UUID restaurantId,
        Instant endDate,
        BigDecimal minValue,
        BigDecimal discountValue,
        DiscountType discountType,
        Instant createdAt
        ) {
    public CouponResponse(Coupon coupon) {
        this(
                coupon.getId(),
                coupon.getName(),
                coupon.getRestaurant() != null ? coupon.getRestaurant().getId() : null,
                coupon.getEndDate(),
                coupon.getMinValue(),
                coupon.getDiscountValue(),
                coupon.getDiscountType(),
                coupon.getCreatedAt()
        );
    }
}
