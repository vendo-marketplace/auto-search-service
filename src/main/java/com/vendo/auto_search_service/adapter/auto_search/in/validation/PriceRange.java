package com.vendo.auto_search_service.adapter.auto_search.in.validation;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@ValidPriceRange
public record PriceRange(

        @DecimalMin(value = "0", message = "Minimal price must not be less than 0.")
        BigDecimal minPrice,

        @DecimalMin(value = "0", message = "Maximum price must not be less than 0.")
        BigDecimal maxPrice

) {
}
