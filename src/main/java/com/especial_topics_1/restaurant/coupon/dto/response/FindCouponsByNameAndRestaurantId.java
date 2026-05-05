package com.especial_topics_1.restaurant.coupon.dto.response;

public record FindCouponsByNameAndRestaurantId(
        CouponResponse restaurantCoupon,
        CouponResponse globalCoupon
) {
}
