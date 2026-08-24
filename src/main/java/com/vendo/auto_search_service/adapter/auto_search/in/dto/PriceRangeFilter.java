package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.core_lib.annotations.price.ValidPriceRange;
import com.vendo.core_lib.dto.request.PriceRange;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@ValidPriceRange
public record PriceRangeFilter(
        @DecimalMin(value = "0", message = "Minimal price must not be less than 0.")
        BigDecimal minPrice,

        @DecimalMin(value = "0", message = "Maximum price must not be less than 0.")
        BigDecimal maxPrice
) implements PriceRange {

    @Override
    public BigDecimal getMinPrice() {
        return minPrice;
    }

    @Override
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public static PriceRangeFilter from(BigDecimal minPrice, BigDecimal maxPrice) {
        return new PriceRangeFilter(minPrice, maxPrice);
    }

}
