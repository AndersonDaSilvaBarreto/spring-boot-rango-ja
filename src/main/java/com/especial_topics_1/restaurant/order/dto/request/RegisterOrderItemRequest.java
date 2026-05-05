package com.especial_topics_1.restaurant.order.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterOrderItemRequest(
        @NotNull(message = "O ID do prato é obrigatório")
        UUID dishId,
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser de pelo menos 1")
        @Max(value = 99, message = "Você não pode pedir mais de 99 unidades do mesmo prato")
        Integer quantity) {

}
