package com.especial_topics_1.restaurant.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken) {
}
