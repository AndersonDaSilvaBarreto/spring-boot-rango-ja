package com.especial_topics_1.restaurant.dish;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DishRepository extends JpaRepository<Dish, UUID> {
    Page<Dish> findAllByRestaurantId(UUID restaurantId, Pageable pageable);
    Optional<Dish> findByRestaurantIdAndId(UUID restaurantId, UUID id);
}
