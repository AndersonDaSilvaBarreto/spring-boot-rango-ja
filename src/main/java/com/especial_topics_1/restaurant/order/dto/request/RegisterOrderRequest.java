package com.especial_topics_1.restaurant.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record RegisterOrderRequest(
        @NotNull(message = "O ID do restaurante é obrigatório")
        UUID restaurantId,

        UUID couponId,

        @Valid
        @NotNull(message = "A lista de itens não pode ser nula")
        @Size(
                min = 1,
                max = 5,
                message = "Um pedido deve ter de 1 a 5 pratos diferentes")
        List<RegisterOrderItemRequest> orderItems

) {
}
