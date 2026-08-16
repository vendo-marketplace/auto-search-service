package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.adapter.auto_search.in.validation.PriceRange;
import com.vendo.auto_search_service.adapter.auto_search.in.validation.ValidExpirationDate;
import com.vendo.auto_search_service.adapter.auto_search.in.validation.ValidPriceRange;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@ValidPriceRange
public record CreateAutoSearchRequest(

        @NotBlank(message = "Category id is required.")
        String categoryId,

        @DecimalMin(value = "0", message = "Minimal price must not be less than 0.")
        BigDecimal minPrice,

        @DecimalMin(value = "0", message = "Maximum price must not be less than 0.")
        BigDecimal maxPrice,

        String address,

        @NotNull(message = "Expiration date is required.")
        @ValidExpirationDate
        LocalDateTime expirationDate

) implements PriceRange {
}
