package com.especial_topics_1.restaurant.restaurant.dto.response;

import com.especial_topics_1.restaurant.restaurant.Restaurant;

import java.time.Instant;
import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        UUID ownerId,
        String name,
        String description,
        Boolean isOpen,
        String logoUrl,
        Instant createdAt
) {
    public RestaurantResponse(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getOwner().getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getIsOpen(),
                restaurant.getLogoUrl(),
                restaurant.getCreatedAt()
        );
    }
}
