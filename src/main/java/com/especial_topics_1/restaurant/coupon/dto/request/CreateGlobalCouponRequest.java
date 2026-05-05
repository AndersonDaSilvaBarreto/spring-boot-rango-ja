package com.especial_topics_1.restaurant.coupon.dto.request;

import com.especial_topics_1.restaurant.coupon.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateGlobalCouponRequest(
        @NotNull
        @Size(min = 3, max = 50,message = "O nome precisa ter entre 3 e 50 caracteres")
        @Pattern(regexp = "^[A-Z0-9]+$", message = "O cupom deve conter apenas letras maiúsculas e números, sem espaços")
        String name,
        @NotNull
        @Future
        Instant endDate,
        @NotNull
        @PositiveOrZero
        BigDecimal minValue,
        @NotNull
        @Positive
        BigDecimal discountValue,
        @NotNull
        DiscountType discountType

) {
}
