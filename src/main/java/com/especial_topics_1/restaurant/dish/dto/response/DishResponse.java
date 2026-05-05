package com.especial_topics_1.restaurant.dish.dto.response;

import com.especial_topics_1.restaurant.dish.Dish;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DishResponse(
        UUID id,
        String name,
        BigDecimal price,
        String imageUrl,
        Integer categoryId,
        Instant createdAt
) {
    public DishResponse(Dish dish) {
        this(
                dish.getId(),
                dish.getName(),
                dish.getPrice(),
                dish.getImageUrl(),
                dish.getCategory().getId(),
                dish.getCreatedAt()
        );
    }
}
