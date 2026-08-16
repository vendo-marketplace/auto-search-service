package com.vendo.auto_search_service.adapter.auto_search.in.dto;

import com.vendo.auto_search_service.adapter.auto_search.in.validation.PriceRange;
import com.vendo.auto_search_service.adapter.auto_search.in.validation.ValidExpirationDate;
import com.vendo.auto_search_service.adapter.auto_search.in.validation.ValidPriceRange;
import com.vendo.auto_search_service.adapter.auto_search.in.validation.ValidUpdateStatus;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@ValidPriceRange
public record UpdateAutoSearchRequest(

        String categoryId,

        @DecimalMin(value = "0", message = "Mininal price must not be less than 0.")
        BigDecimal minPrice,

        @DecimalMin(value = "0", message = "Maximum price must not be less than 0.")
        BigDecimal maxPrice,

        String address,

        @ValidUpdateStatus
        SearchStatus status,

        @ValidExpirationDate
        LocalDateTime expirationDate

) implements PriceRange {
}
