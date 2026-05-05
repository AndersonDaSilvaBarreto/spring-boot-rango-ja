package com.especial_topics_1.restaurant.restaurant.dto.request;

import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record UpdateRestaurantRequest(
        @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres.")
        String name,

        @Size(min = 15,max = 500, message = "A descrição deve ter entre 15 e 500 caracteres.")
        String description,


        Boolean isOpen,


        MultipartFile logo
) {
}
