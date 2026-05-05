package com.especial_topics_1.restaurant.order.dto.request;

import com.especial_topics_1.restaurant.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "O novo status é obrigatório")
        OrderStatus status
) {
}
