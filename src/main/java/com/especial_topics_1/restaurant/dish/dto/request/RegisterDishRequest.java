package com.especial_topics_1.restaurant.dish.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record RegisterDishRequest(
        @NotBlank(message = "O nome do prato é obrigatório.")
        @Size(min = 2, max = 255, message = "O nome precisa de 2 a 255 caracteres" )
       String name,
        @NotNull(message = "O preço é obrigatório")
        @PositiveOrZero
        BigDecimal price,
        @NotNull(message = "A categoria é obrigatória.")
        Integer categoryId,
        MultipartFile image
) {
}
