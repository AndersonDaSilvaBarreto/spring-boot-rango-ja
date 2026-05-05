package com.especial_topics_1.restaurant.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record RegisterRestaurantRequest(
        @NotBlank(message = "O nome do restaurante é obrigatório")
        @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres.")
        String name,
        @Size(min = 15,max = 500, message = "A descrição deve ter entre 15 e 500 caracteres.")
        String description,
        MultipartFile logo
) {
}
