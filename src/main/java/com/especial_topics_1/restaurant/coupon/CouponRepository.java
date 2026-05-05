package com.especial_topics_1.restaurant.coupon;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    boolean existsByNameAndRestaurantId(String name, UUID restaurantId);

    @Query("SELECT COUNT(c) > 0 FROM Coupon c WHERE c.name = :name AND c.restaurant IS NULL")
    boolean existsGlobalCoupon(@Param("name") String name);

    @Query("SELECT c FROM Coupon c " +
            "WHERE c.name = :name " +
            "AND (c.restaurant.id = :restaurantId OR c.restaurant IS NULL) " +
            "AND c.endDate > CURRENT_TIMESTAMP")
    List<Coupon> findCouponsByNameAndScope(
            @Param("name") String name,
            @Param("restaurantId") UUID restaurantId);

    @Query("SELECT c FROM Coupon c " +
            "WHERE (c.restaurant.id = :restaurantId OR c.restaurant IS NULL) " +
            "AND c.endDate > CURRENT_TIMESTAMP ")
    Page<Coupon> findValidCouponsByRestaurantIdOrRestaurantNull(
            @Param("restaurantId") UUID restaurantId,
            Pageable pageable

    );
}
