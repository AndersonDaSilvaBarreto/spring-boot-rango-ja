package com.especial_topics_1.restaurant.dish.dto.request;


import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record UpdateDishRequest(
        @Size(min = 2, max = 255, message = "O nome precisa ter de 2 a 255 caracteres")
        String name,

        @PositiveOrZero
        BigDecimal price,

        @PositiveOrZero
        Integer categoryId,

        MultipartFile image
) {
}
