package com.especial_topics_1.restaurant.auth.dto.response;

import com.especial_topics_1.restaurant.user.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role,
        UUID restaurantId
) {}

