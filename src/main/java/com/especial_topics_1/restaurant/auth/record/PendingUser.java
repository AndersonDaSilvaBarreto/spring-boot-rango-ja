package com.especial_topics_1.restaurant.auth.record;

public record PendingUser(
        String name,
        String email,
        String passwordHash,
        String phone,
        String code
) {
}
