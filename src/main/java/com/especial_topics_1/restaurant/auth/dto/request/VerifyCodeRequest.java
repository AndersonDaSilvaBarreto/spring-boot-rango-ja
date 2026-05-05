package com.especial_topics_1.restaurant.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyCodeRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "O código é obrigatório")
        @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos")
        String code
) {
}
