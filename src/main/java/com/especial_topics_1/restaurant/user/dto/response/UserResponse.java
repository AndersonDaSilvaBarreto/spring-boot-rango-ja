package com.especial_topics_1.restaurant.user.dto.response;


import com.especial_topics_1.restaurant.user.Role;
import com.especial_topics_1.restaurant.user.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        Role role,
        Instant createdAt,
        UUID restaurantId
) {
    public UserResponse(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt(),
                null
        );
    }
}
